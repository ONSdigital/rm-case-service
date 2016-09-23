package uk.gov.ons.ctp.response.casesvc.service.impl;

import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.RESPONDED;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseProjection;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public class CaseServiceImpl implements CaseService {

  private static final int TRANSACTION_TIMEOUT = 30;

  /**
   * Spring Data Repository for Case entities.
   */
  @Inject
  private CaseRepository caseRepo;

  /**
   * Spring Data Repository for Questionnaire Entities.
   */
  @Inject
  private QuestionnaireRepository questionnaireRepo;

  /**
   * Spring Data Repository for CaseEvent Entities.
   */
  @Inject
  private CaseEventRepository caseEventRepo;

  /**
   * Spring Data Repository for Category Entities.
   */
  @Inject
  private CategoryRepository categoryRepo;

  /**
   * ActionSVC client service
   */
  @Inject
  private ActionSvcClientService actionSvcClientService;

  /**
   * Notification publishing service for Case life cycle events
   */
  @Inject
  private CaseNotificationPublisher notificationPublisher;

  @Override
  public List<Case> findCasesByUprn(final Long uprn) {
    log.debug("Entering findCasesByUprn with uprn {}", uprn);
    return caseRepo.findByUprn(uprn);
  }

  @Override
  public Case findCaseByQuestionnaireId(final Integer qid) {
    log.debug("Entering findCaseByQuestionnaireId");
    Questionnaire questionnaire = questionnaireRepo.findByQuestionnaireId(qid);
    if (questionnaire == null) {
      return null;
    }
    return caseRepo.findOne(questionnaire.getCaseId());
  }

  @Override
  public Case findCaseByCaseId(final Integer caseId) {
    log.debug("Entering findCaseByCaseId");
    return caseRepo.findOne(caseId);
  }

  @Override
  public List<Integer> findCaseIdsByStatesAndActionPlanId(final List<CaseDTO.CaseState> caseStates,
      final Integer actionPlanId) {
    log.debug("Entering findCaseByStatesAndActionPlanId");
    List<CaseDTO.CaseState> states = caseStates;
    if (CollectionUtils.isEmpty(states)) {
      states = Arrays.asList(CaseDTO.CaseState.values());
    }
    List<CaseProjection> caseProjections = caseRepo.findCaseIdByStateInAndActionPlanId(states, actionPlanId);
    return caseProjections.stream().map(cp->cp.getCaseId()).collect(Collectors.toList());
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
    Case existingCase = caseRepo.findOne(caseId);

    if (existingCase != null) {
      caseEvent.setCreatedDateTime(DateTimeUtil.nowUTC());
      createdCaseEvent = caseEventRepo.save(caseEvent);

      Category category = categoryRepo.findByName(caseEvent.getCategory());
      Boolean closeCase = category.getCloseCase();

      CategoryDTO.CategoryName reasonForClosure = CategoryDTO.CategoryName.getEnumByLabel(caseEvent.getCategory());

      if (Boolean.TRUE.equals(closeCase)) {
        closeCase(existingCase);
      }

      if (reasonForClosure == CategoryDTO.CategoryName.QUESTIONNAIRE_RESPONSE) {
        markQuestionnairesAsResponded(caseId);
      }

      String actionType = category.getGeneratedActionType();
      if (!StringUtils.isEmpty(actionType)) {
        actionSvcClientService.createAndPostAction(actionType, caseId, caseEvent.getCreatedBy());
      }
    }
    return createdCaseEvent;
  }

  private void closeCase(Case caze) {
    caseRepo.setState(caze.getCaseId(), CaseDTO.CaseState.RESPONDED.name());
    actionSvcClientService.cancelActions(caze.getCaseId());
    notificationPublisher
        .sendNotifications(Arrays.asList(new CaseNotification(caze.getCaseId(), caze.getActionPlanId(), RESPONDED)));

  }

  /**
   * mark all case related questionnaires as responded
   *
   * @param caseId Integer case ID
   */
  private void markQuestionnairesAsResponded(int caseId) {
    Timestamp currentTime = DateTimeUtil.nowUTC();
    List<Questionnaire> associatedQuestionnaires = questionnaireRepo.findByCaseId(caseId);
    for (Questionnaire questionnaire : associatedQuestionnaires) {
      questionnaireRepo.setResponseDatetimeFor(currentTime, questionnaire.getQuestionnaireId());
    }
  }
}
