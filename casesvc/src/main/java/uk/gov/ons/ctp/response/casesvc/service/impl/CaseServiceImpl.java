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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Contact;
import uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionPlanMappingRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
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

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(final CaseEvent caseEvent, final Case newCase) {
    log.debug("Entering createCaseEvent with caseEvent {}", caseEvent);

    CaseEvent createdCaseEvent = null;
    Integer caseId = caseEvent.getCaseId();
    Case targetCase = caseRepo.findOne(caseId);

    if (targetCase != null) {
      Category category = categoryRepo.findOne(caseEvent.getCategory());

      // save the case event to db
      caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
      createdCaseEvent = caseEventRepo.save(caseEvent);

      // do we need to record a response?
      checkAndEffectRecordinfOfResponse(category, targetCase);

      // does the event transition the case?
      checkAndEffectOriginalCaseStateTransition(category, targetCase);
      
      // should we create an ad hoc action?
      checkAndEffectAdHocActionCreation(category, caseId, caseEvent);

      // should a new case be created?
      checkAndEffectCreationOfNewCase(category, caseEvent, caseId, targetCase, newCase);
    }
    return createdCaseEvent;
  }

  private void checkAndEffectCreationOfNewCase(Category category, CaseEvent caseEvent, Integer caseId, Case targetCase,
      Case newCase) {
    if (category.getNewCaseRespondentType() != null) {
      if (newCase == null) {
        throw new RuntimeException(String.format(MISSING_NEW_CASE_MSG, caseId));
      } else {
        Case persistedNewCase = createNewCaseFromEvent(caseEvent, targetCase, newCase);
        log.debug("Newly created case has id of {} and ref of {}", persistedNewCase.getCaseId(),
            persistedNewCase.getCaseRef());
      }
    }
  }

  private void checkAndEffectRecordinfOfResponse(Category category, Case targetCase) {
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

  private void checkAndEffectAdHocActionCreation(Category category, Integer caseId, CaseEvent caseEvent) {
    String actionType = category.getGeneratedActionType();
    log.debug("actionType = {}", actionType);
    if (!StringUtils.isEmpty(actionType)) {
      actionSvcClientService.createAndPostAction(actionType, caseId, caseEvent.getCreatedBy());
    }
  }

  private void checkAndEffectOriginalCaseStateTransition(Category category, Case targetCase) {
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
        notifyActionService(targetCase, transitionEvent);
        if (transitionEvent == CaseDTO.CaseEvent.DISABLED) {
          internetAccessCodeSvcClientService.disableIAC(targetCase.getIac());
        }
      }
    }
  }

  @Override
  public CaseNotification prepareCaseNotification(Case caze, CaseDTO.CaseEvent transitionEvent) {
    ActionPlanMapping actionPlanMapping = actionPlanMappingRepo.findOne(caze.getActionPlanMappingId());
    NotificationType notifType = NotificationType.valueOf(transitionEvent.name());
    return new CaseNotification(caze.getCaseId(), actionPlanMapping.getActionPlanId(), notifType);
  }

  private Case createNewCaseFromEvent(CaseEvent caseEvent, Case originalCase, Case newCase) {
    newCase.setState(CaseDTO.CaseState.REPLACEMENT_INIT);
    newCase.setCreatedDateTime(DateTimeUtil.nowUTC());
    newCase.setCaseGroupId(originalCase.getCaseGroupId());
    newCase.setCreatedBy(caseEvent.getCreatedBy());
    return caseRepo.saveAndFlush(newCase);
  }

  private void notifyActionService(Case caze, CaseDTO.CaseEvent transitionEvent) {
    notificationPublisher.sendNotifications(Arrays.asList(prepareCaseNotification(caze, transitionEvent)));
  }

  private Case recordResponse(Case caze, InboundChannel channel) {
    log.debug("Entering recordResponse with caze {} and channel {}", caze, channel);
    // create a Response obj and associate it with this case
    Response response = Response.builder()
        .inboundChannel(channel)
        .caseId(caze.getCaseId())
        .dateTime(DateTimeUtil.nowUTC()).build();

    caze.getResponses().add(response);
    caseRepo.save(caze);
    return caze;
  }

}
