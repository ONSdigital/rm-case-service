package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.state.StateTransitionException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionPlanMappingRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseTypeRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public class CaseServiceImpl implements CaseService {

  private static final String IAC_OVERUSE_MSG = "More than one case found to be using IAC %s";
  private static final String MISSING_NEW_CASE_MSG = "New Case definition missing for original case %s";
  private static final String CASE_NO_LONGER_ACTIONABLE_MSG = "The Case %s is no longer actionable - the requested event is invalid";
  private static final String WRONG_NEW_CASE_TYPE_MSG = "New Case definition has incorrect casetype (new respondent type '%s' is not required type '%s')";
  private static final String WRONG_OLD_CASE_TYPE_MSG = "Old Case definition has incorrect casetype (old respondent type '%s' is not expected type '%s')";

  private static final int TRANSACTION_TIMEOUT = 30;

  @Inject
  private CaseRepository caseRepo;

  @Inject
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  @Inject
  private ActionPlanMappingRepository actionPlanMappingRepo;

  @Inject
  private CaseEventRepository caseEventRepo;

  @Inject
  private CaseTypeRepository caseTypeRepo;

  @Inject
  private CategoryRepository categoryRepo;

  @Inject
  private ActionSvcClientService actionSvcClientService;

  @Inject
  private InternetAccessCodeSvcClientService internetAccessCodeSvcClientService;

  @Inject
  private CaseNotificationPublisher notificationPublisher;

  @Override
  public Case findCaseByCaseId(final Integer caseId) {
    log.debug("Entering findCaseByCaseId");
    return caseRepo.findOne(caseId);
  }

  @Override
  public Case findCaseByCaseRef(final String caseRef) {
    log.debug("Entering findCaseByCaseRef");
    return caseRepo.findByCaseRef(caseRef);
  }

  @Override
  public Case findCaseByIac(final String iac) {
    log.debug("Entering findCaseByIac");

    List<Case> cases = caseRepo.findByIac(iac);
    Case caze = null;
    if (!CollectionUtils.isEmpty(cases)) {
      if (cases.size() != 1) {
        throw new RuntimeException(String.format(IAC_OVERUSE_MSG, iac));
      }
      caze = cases.get(0);
    }
    return caze;
  }

  @Override
  public List<Case> findCasesByCaseGroupId(final Integer caseGroupId) {
    log.debug("Entering findCasesByCaseGroupId");
    return caseRepo.findByCaseGroupId(caseGroupId);
  }

  @Override
  public List<CaseEvent> findCaseEventsByCaseId(final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return caseEventRepo.findByCaseIdOrderByCreatedDateTimeDesc(caseId);
  }

  @Override
  public CaseNotification prepareCaseNotification(Case caze, CaseDTO.CaseEvent transitionEvent) {
    ActionPlanMapping actionPlanMapping = actionPlanMappingRepo.findOne(caze.getActionPlanMappingId());
    NotificationType notifType = NotificationType.valueOf(transitionEvent.name());
    return new CaseNotification(caze.getCaseId(), actionPlanMapping.getActionPlanId(), notifType);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Case newCase) {
    log.debug("Entering createCaseEvent with caseEvent {}", caseEvent);

    CaseEvent createdCaseEvent = null;
    Case targetCase = caseRepo.findOne(caseEvent.getCaseId());

    if (targetCase != null) {
      Category category = categoryRepo.findOne(caseEvent.getCategory());

      // fail fast...
      validateCaseEventRequest(category, caseEvent, targetCase, newCase);

      // save the case event to db
      caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
      createdCaseEvent = caseEventRepo.save(caseEvent);

      // do we need to record a response?
      checkAndEffectRecordingOfResponse(category, targetCase);

      // does the event transition the case?
      checkAndEffectTargetCaseStateTransition(category, targetCase);

      // should we create an ad hoc action?
      checkAndEffectAdHocActionCreation(category, caseEvent);

      // should a new case be created?
      checkAndEffectCreationOfNewCase(category, caseEvent, targetCase, newCase);
    }
    return createdCaseEvent;
  }

  /**
   * Upfront fail fast validation - if this event is going to require a new case
   * to be created, lets check the request is valid before we do something we
   * cannot rollback ie IAC disable, or Action creation.
   * 
   * @param category the category details
   * @param caseEvent the event details
   * @param targetCase the case the event is being created against
   * @param newCase the details provided in the event request for the new case
   */
  private void validateCaseEventRequest(Category category, CaseEvent caseEvent, Case targetCase,
      Case newCase) {

    if (category.getNewCaseRespondentType() != null) {
      if (newCase == null) {
        throw new RuntimeException(String.format(MISSING_NEW_CASE_MSG, targetCase.getCaseId()));
      }
      CaseType targetCaseType = caseTypeRepo.findOne(targetCase.getCaseTypeId());
      checkRespondentTypesMatch(WRONG_OLD_CASE_TYPE_MSG, category.getOldCaseRespondentType(), targetCaseType.getRespondentType());

      CaseType intendedCaseType = caseTypeRepo.findOne(newCase.getCaseTypeId());
      checkRespondentTypesMatch(WRONG_NEW_CASE_TYPE_MSG, category.getNewCaseRespondentType(), intendedCaseType.getRespondentType());
    }
   
    if (!StringUtils.isEmpty(category.getGeneratedActionType()) && targetCase.getState().equals(CaseDTO.CaseState.INACTIONABLE)) {
        throw new RuntimeException(String.format(CASE_NO_LONGER_ACTIONABLE_MSG, targetCase.getCaseId()));
    }
    
  }

  /**
   * Simple method to compare two respondent types and complain if they don't
   * 
   * @param msg the error message to use if they mismatch
   * @param respondentTypeA the type on the left
   * @param respondentTypeB the type on the right
   */
  private void checkRespondentTypesMatch(String msg, String respondentTypeA, String respondentTypeB) {
    if (!respondentTypeA.equals(respondentTypeB)) {
      throw new RuntimeException(String.format(msg, respondentTypeA, respondentTypeB));
    }
  }

  /**
   * Check to see if a new case creation is indicated by the event category and
   * if so create it
   * 
   * @param category the category details of the event
   * @param caseEvent the basic event
   * @param targetCase the 'source' case the event is being created for
   * @param newCase the details for the new case (if indeed one is required)
   *          else null
   */
  private void checkAndEffectCreationOfNewCase(Category category, CaseEvent caseEvent, Case targetCase,
      Case newCase) {
    if (category.getNewCaseRespondentType() != null) {
      createNewCaseFromEvent(caseEvent, targetCase, newCase);
    }
  }

  /**
   * Check to see if the event requires a response to be recorded for the case
   * and if so ... record it
   * 
   * @param category the category details of the event
   * @param targetCase the 'source' case the event is being created for
   */
  private void checkAndEffectRecordingOfResponse(Category category, Case targetCase) {
    // create and add Response obj to the case if event is a response
    switch (category.getCategoryType()) {
    case ONLINE_QUESTIONNAIRE_RESPONSE:
      recordResponse(targetCase, InboundChannel.ONLINE);
      break;
    case PAPER_QUESTIONNAIRE_RESPONSE:
      recordResponse(targetCase, InboundChannel.PAPER);
      break;
    default:
      break;
    }
  }

  /**
   * Send a request to the action service to create an ad-hoc action for the
   * event if required
   * 
   * @param category the category details of the event
   * @param caseEvent the basic event
   */
  private void checkAndEffectAdHocActionCreation(Category category, CaseEvent caseEvent) {
    String actionType = category.getGeneratedActionType();
    log.debug("actionType = {}", actionType);
    if (!StringUtils.isEmpty(actionType)) {
      actionSvcClientService.createAndPostAction(actionType, caseEvent.getCaseId(), caseEvent.getCreatedBy());
    }
  }

  /**
   * Effect a state transition for the target case if the category indicates one
   * is required If a transition was made and the state changes as a result,
   * notify the action service of the state change AND if the event was type
   * DISABLED then also call the IAC service to disable/deactivate the IAC code
   * related to the target case.
   * 
   * @param category the category details of the event
   * @param targetCase the 'source' case the event is being created for
   */
  private void checkAndEffectTargetCaseStateTransition(Category category, Case targetCase) {
    CaseDTO.CaseEvent transitionEvent = category.getEventType();
    if (transitionEvent != null) {
      CaseDTO.CaseState oldState = targetCase.getState();
      CaseDTO.CaseState newState = null;
      try {
        // make the transition
        newState = caseSvcStateTransitionManager.transition(targetCase.getState(), transitionEvent);
        targetCase.setState(newState);
        caseRepo.saveAndFlush(targetCase);
      } catch (StateTransitionException ste) {
        throw new RuntimeException(ste);
      }

      // was a state change effected?
      if (oldState != newState) {
        notificationPublisher.sendNotifications(Arrays.asList(prepareCaseNotification(targetCase, transitionEvent)));
        if (transitionEvent == CaseDTO.CaseEvent.DISABLED) {
          internetAccessCodeSvcClientService.disableIAC(targetCase.getIac());
        }
      }
    }
  }

  /**
   * Go ahead and create a new case using the new case details, associate it
   * with the target case and create the CASE_CREATED event on the new case
   * 
   * @param caseEvent the basic event
   * @param targetCase the 'source' case the event is being created for
   * @param newCase the details for the new case (if indeed one is required)
   *          else null
   * @return the new case
   */
  private Case createNewCaseFromEvent(CaseEvent caseEvent, Case targetCase, Case newCase) {
    Case persistedCase = null;

    newCase.setState(CaseDTO.CaseState.REPLACEMENT_INIT);
    newCase.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCase.setCaseGroupId(targetCase.getCaseGroupId());
    newCase.setCreatedBy(caseEvent.getCreatedBy());
    newCase.setSourceCaseId(targetCase.getCaseId());
    persistedCase = caseRepo.saveAndFlush(newCase);

    CaseEvent newCaseCaseEvent = new CaseEvent();
    newCaseCaseEvent.setCaseId(persistedCase.getCaseId());
    newCaseCaseEvent.setCategory(CategoryDTO.CategoryType.CASE_CREATED);
    newCaseCaseEvent.setCreatedBy("SYSTEM");
    newCaseCaseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    Category caseEventCategory = categoryRepo.findOne(caseEvent.getCategory());
    newCaseCaseEvent.setDescription(String.format("Case created when %s", caseEventCategory.getShortDescription()));

    caseEventRepo.saveAndFlush(newCaseCaseEvent);
    return persistedCase;
  }

  /**
   * Record the online/paper response against the case
   * 
   * @param caze the case
   * @param channel the response channel used
   * @return the modified case
   */
  private Case recordResponse(Case caze, InboundChannel channel) {
    log.debug("Entering recordResponse with caze {} and channel {}", caze, channel);
    // create a Response obj and associate it with this case
    Response response = Response.builder()
        .inboundChannel(channel)
        .caseId(caze.getCaseId())
        .dateTime(DateTimeUtil.nowUTC()).build();

    caze.getResponses().add(response);
    return caseRepo.save(caze);
  }

}
