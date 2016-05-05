package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.service.ActionSvcClientService;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public final class CaseServiceImpl implements CaseService {

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

  @Override
  public List<Case> findCasesByUprn(final Integer uprn) {
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
  public List<BigInteger> findCaseIdsByStateAndActionPlanId(final String caseState, final Integer actionPlanId) {
    log.debug("Entering findCaseByStateAndActionPlanId");
    String stateParam = (caseState == null) ? "%" : caseState;
    return caseRepo.findCaseIdsByStateAndActionPlanId(stateParam, actionPlanId);
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
    log.debug("existingCase = {}", existingCase);
    if (existingCase != null) {
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      caseEvent.setCreatedDateTime(currentTime);
      log.debug("about to create the caseEvent for {}", caseEvent);
      createdCaseEvent = caseEventRepo.save(caseEvent);
      // determine if the Category in this CaseEvent indicates we should close
      // cases
      String categoryName = caseEvent.getCategory();
      Category category = categoryRepo.findByName(categoryName);
      Boolean closeCase = category.getCloseCase();
      log.debug("closeCase = {}", closeCase);

      if (Boolean.TRUE.equals(closeCase)) {
        closeCase(caseId);
        actionSvcClientService.cancelActions(caseId);
      } else {
        actionSvcClientService.createAndPostAction(category, caseId, caseEvent);
      }
    }

    return createdCaseEvent;
  }

  /**
   * Close the Case
   * 
   * @param caseId Integer case ID
   */
  private void closeCase(int caseId) {

    Timestamp currentTime = new Timestamp(System.currentTimeMillis());

    caseRepo.setStatusFor(QuestionnaireServiceImpl.CLOSED, caseId);
    log.debug("parent case marked closed");
    List<Questionnaire> associatedQuestionnaires = questionnaireRepo.findByCaseId(caseId);
    for (Questionnaire questionnaire : associatedQuestionnaires) {
      questionnaireRepo.setResponseDatetimeFor(currentTime, questionnaire.getQuestionnaireId());
    }
    log.debug("all associated Questionnaires marked closed");
  }

}
