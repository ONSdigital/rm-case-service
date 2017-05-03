package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public class CaseServiceImpl implements CaseService {

  private static final String CASE_CREATED_EVENT_DESCRIPTION = "Case created when %s";
  private static final String IAC_OVERUSE_MSG = "More than one case found to be using IAC %s";
  private static final String MISSING_NEW_CASE_MSG = "New Case definition missing for case %s";
  private static final String WRONG_NEW_CASE_TYPE_MSG = "New Case definition has incorrect casetype (new respondent type '%s' is not required type '%s')";
  private static final String WRONG_OLD_CASE_TYPE_MSG = "Old Case definition has incorrect casetype (old respondent type '%s' is not expected type '%s')";

  private static final int TRANSACTION_TIMEOUT = 30;

  @Inject
  private CaseRepository caseRepo;

  @Inject
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  @Inject
  private CaseEventRepository caseEventRepo;

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
    return caseRepo.findByCaseGroupIdOrderByCreatedDateTimeDesc(caseGroupId);
  }

  @Override
  public List<CaseEvent> findCaseEventsByCaseId(final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return caseEventRepo.findByCaseIdOrderByCreatedDateTimeDesc(caseId);
  }

  @Override
  public CaseNotification prepareCaseNotification(Case caze, CaseDTO.CaseEvent transitionEvent) {
    //TODO BRES reinstate this method, filling in the action plan id from case itself?
    
//    ActionPlanMapping actionPlanMapping = actionPlanMappingRepo.findOne(caze.getActionPlanMappingId());
//    NotificationType notifType = NotificationType.valueOf(transitionEvent.name());
//    return new CaseNotification(caze.getCaseId(), actionPlanMapping.getActionPlanId(), notifType);
    return null;
  }

  /**
   * This is where it all happens kids. After the creation of the cases from
   * sample and their subsequent distribution, this is where everything happens.
   * Anything that happens to the case from then on is thru events - created
   * here.
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Case newCase) {
    return createCaseEvent(caseEvent, newCase, DateTimeUtil.nowUTC());
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(CaseEvent caseEvent, Case newCase, Timestamp timestamp) {
    log.debug("Entering createCaseEvent with caseEvent {}", caseEvent);
    log.info("SPLUNK: CaseEventCreation: caseId={}, category={}, subCategory={}, createdBy={}",
        caseEvent.getCaseId(),
        caseEvent.getCategory(),
        caseEvent.getSubCategory(),
        caseEvent.getCreatedBy());

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
      recordCaseResponse(category, targetCase, timestamp);

      // does the event transition the case?
      effectTargetCaseStateTransition(category, targetCase);

      // should we create an ad hoc action?
      createAdHocAction(category, caseEvent);

      // should a new case be created?
      createNewCase(category, caseEvent, targetCase, newCase);
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
      // TODO BRES replace with code to compare sampleCaseType from Case itself?
//      CaseType targetCaseType = caseTypeRepo.findOne(targetCase.getCaseTypeId());
//      checkRespondentTypesMatch(WRONG_OLD_CASE_TYPE_MSG,
//          targetCaseType.getRespondentType(), category.getOldCaseRespondentType());
//
//      CaseType intendedCaseType = caseTypeRepo.findOne(newCase.getCaseTypeId());
//      checkRespondentTypesMatch(WRONG_NEW_CASE_TYPE_MSG, category.getNewCaseRespondentType(),
//          intendedCaseType.getRespondentType());
    }
  }

  /**
   * Simple method to compare two respondent types and complain if they don't
   * 
   * @param msg the error message to use if they mismatch
   * @param newRespondentType the type on the left
   * @param expectedRespondentType the type on the right
   */
  private void checkRespondentTypesMatch(String msg, String newRespondentType, String expectedRespondentType) {
    if (!newRespondentType.equals(expectedRespondentType)) {
      throw new RuntimeException(String.format(msg, newRespondentType, expectedRespondentType));
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
  private void createNewCase(Category category, CaseEvent caseEvent, Case targetCase,
      Case newCase) {
    if (category.getNewCaseRespondentType() != null) {
      createNewCaseFromEvent(caseEvent, targetCase, newCase, category);
    }
  }

  /**
   * Check to see if the event requires a response to be recorded for the case
   * and if so ... record it
   * 
   * @param category the category details of the event
   * @param targetCase the 'source' case the event is being created for
   * @param timestamp timestamp the timestamp of the CaseResponse
   */
  private void recordCaseResponse(Category category, Case targetCase, Timestamp timestamp) {
    InboundChannel channel = null;
    switch (category.getCategoryType()) {
      case ONLINE_QUESTIONNAIRE_RESPONSE:
        channel = InboundChannel.ONLINE;
        break;
      case PAPER_QUESTIONNAIRE_RESPONSE:
        channel = InboundChannel.PAPER;
        break;
      default:
        break;
    }
    if (channel != null) {
      Response response = Response.builder()
          .inboundChannel(channel)
          .caseId(targetCase.getCaseId())
          .dateTime(timestamp).build();

      targetCase.getResponses().add(response);
      caseRepo.save(targetCase);
    }
  }

  /**
   * Send a request to the action service to create an ad-hoc action for the
   * event if required
   * 
   * @param category the category details of the event
   * @param caseEvent the basic event
   */
  private void createAdHocAction(Category category, CaseEvent caseEvent) {
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
  private void effectTargetCaseStateTransition(Category category, Case targetCase) {
    CaseDTO.CaseEvent transitionEvent = category.getEventType();
    if (transitionEvent != null) {
      // case might have transitioned from actionable to inactionable prev via deactivated
      // so newstate == oldstate, but always want to disable iac if event is  disabled ie as the result
      // of an online response after a refusal
      if (transitionEvent == CaseDTO.CaseEvent.DISABLED) {
        internetAccessCodeSvcClientService.disableIAC(targetCase.getIac());
      }
      CaseDTO.CaseState oldState = targetCase.getState();
      CaseDTO.CaseState newState = null;
      // make the transition
      newState = caseSvcStateTransitionManager.transition(targetCase.getState(), transitionEvent);
      // was a state change effected?
      if (oldState != newState) {
        targetCase.setState(newState);
        caseRepo.saveAndFlush(targetCase);
        notificationPublisher.sendNotifications(Arrays.asList(prepareCaseNotification(targetCase, transitionEvent)));
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
   * @param caseEventCategory the caseEventCategory
   * @return the new case
   */
  private Case createNewCaseFromEvent(CaseEvent caseEvent, Case targetCase, Case newCase, Category caseEventCategory) {
    Case persistedCase = saveNewCase(caseEvent, targetCase, newCase);
    // NOTE the action service does not need to be notified of the creation of
    // the new case - yet
    // That will be done when the CaseDistributor wakes up and assigns an IAC to
    // the newly created case
    // ie it might be created here, but it is not yet ready for prime time
    // without its IAC!
    createCaseCreatedEvent(persistedCase, caseEventCategory);
    return persistedCase;
  }

  /**
   * Create a new case row for a replacement/new case
   * 
   * @param caseEvent the event that lead to the creation of the new case
   * @param targetCase the case the caseEvent was applied to
   * @param newCase the case we have been asked to create off the back of the
   *          event
   * @return the persisted case
   */
  private Case saveNewCase(CaseEvent caseEvent, Case targetCase, Case newCase) {
    newCase.setState(CaseDTO.CaseState.REPLACEMENT_INIT);
    newCase.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCase.setCaseGroupId(targetCase.getCaseGroupId());
    newCase.setCreatedBy(caseEvent.getCreatedBy());
    newCase.setSourceCaseId(targetCase.getCaseId());
    return caseRepo.saveAndFlush(newCase);
  }

  /**
   * Create an event for a newly created case
   * 
   * @param caze the case for which we want to record the event
   * @param caseEventCategory the category of the event that led to the creation
   *          of the case
   * @return the created event
   */
  private CaseEvent createCaseCreatedEvent(Case caze, Category caseEventCategory) {
    CaseEvent newCaseCaseEvent = new CaseEvent();
    newCaseCaseEvent.setCaseId(caze.getCaseId());
    newCaseCaseEvent.setCategory(CategoryDTO.CategoryType.CASE_CREATED);
    newCaseCaseEvent.setCreatedBy(Constants.SYSTEM);
    newCaseCaseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCaseCaseEvent
        .setDescription(String.format(CASE_CREATED_EVENT_DESCRIPTION, caseEventCategory.getShortDescription()));

    caseEventRepo.saveAndFlush(newCaseCaseEvent);
    return newCaseCaseEvent;
  }
}
