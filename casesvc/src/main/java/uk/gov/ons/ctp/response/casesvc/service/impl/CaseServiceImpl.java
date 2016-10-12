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
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public class CaseServiceImpl implements CaseService {

  private static final String IAC_OVERUSE_MSG = "More than one case found to be using IAC %s";
  private static final int TRANSACTION_TIMEOUT = 30;

  @Inject
  private CaseRepository caseRepo;

  @Inject
  private StateTransitionManager<CaseDTO.CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  @Inject
  private ActionPlanMappingService actionPlanMappingService;

  @Inject
  private CaseEventRepository caseEventRepo;

  @Inject
  private CategoryRepository categoryRepo;

  @Inject
  private ActionSvcClientService actionSvcClientService;

  @Inject
  private InternetAccessCodeSvcClientServiceImpl internetAccessCodeSvcClientServiceImpl;

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
  public CaseEvent createCaseEvent(final CaseEvent caseEvent) {
    log.debug("Entering createCaseEvent");
    CaseEvent createdCaseEvent = null;

    Integer caseId = caseEvent.getCaseId();
    Case targetCase = caseRepo.findOne(caseId);

    if (targetCase != null) {
      // save the case event to db
      caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
      createdCaseEvent = caseEventRepo.save(caseEvent);

      Category category = categoryRepo.findOne(caseEvent.getCategory());
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

      // does the event transition the case?
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
            internetAccessCodeSvcClientServiceImpl.disableIAC(targetCase.getIac());
          }
        }
      }

      // should the event create an ad-hoc action?
      String actionType = category.getGeneratedActionType();
      if (!StringUtils.isEmpty(actionType)) {
        actionSvcClientService.createAndPostAction(actionType, caseId, caseEvent.getCreatedBy());
      }
    }
    return createdCaseEvent;
  }

  @Override
  public CaseNotification prepareCaseNotification(Case caze, CaseDTO.CaseEvent transitionEvent) {
    ActionPlanMapping actionPlanMapping = actionPlanMappingService.findActionPlanMapping(caze.getActionPlanMappingId());
    NotificationType notifType = NotificationType.valueOf(transitionEvent.name());
    return new CaseNotification(caze.getCaseId(), actionPlanMapping.getActionPlanId(), notifType);
  }

  private void notifyActionService(Case caze, CaseDTO.CaseEvent transitionEvent) {
    notificationPublisher.sendNotifications(Arrays.asList(prepareCaseNotification(caze, transitionEvent)));
  }

  private Case recordResponse(Case caze, InboundChannel channel) {
    // create a Response obj and associate it with this case
    Response response = Response.builder()
        .inboundChannel(channel)
        .caze(caze)
        .dateTime(DateTimeUtil.nowUTC()).build();

    caze.getResponses().add(response);
    caseRepo.save(caze);
    return caze;
  }

}
