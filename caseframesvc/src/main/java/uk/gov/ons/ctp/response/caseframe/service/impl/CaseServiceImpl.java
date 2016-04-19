package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.rest.RestClient;
import uk.gov.ons.ctp.response.action.representation.ActionDTO;
import uk.gov.ons.ctp.response.caseframe.config.AppConfig;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public final class CaseServiceImpl implements CaseService {

  private static final int TRANSACTION_TIMEOUT = 30;

  @Inject
  private AppConfig appConfig;

  @Inject
  private RestClient actionSvcRestClient;

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
  private CaseEventRepository caseEventRepository;

  /**
   * Spring Data Repository for Category Entities.
   */
  @Inject
  private CategoryRepository categoryRepo;


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
  public List<CaseEvent> findCaseEventsByCaseId(final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return caseEventRepository.findByCaseIdOrderByCreatedDateTimeDesc(caseId);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public CaseEvent createCaseEvent(final CaseEvent caseEvent) {
    log.debug("Entering createCaseEvent");
    CaseEvent result = null;

    Integer parentCaseId = caseEvent.getCaseId();
    Case parentCase = caseRepo.findOne(parentCaseId);
    log.debug("parentCase = {}", parentCase);
    if (parentCase != null) {
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
      caseEvent.setCreatedDateTime(currentTime);
      log.debug("about to create the caseEvent for {}", caseEvent);
      result =  caseEventRepository.save(caseEvent);

      String categoryName = caseEvent.getCategory();
      Category category = categoryRepo.findByName(categoryName);
      Boolean closeCase = category.getCloseCase();
      log.debug("closeCase = {}", closeCase);
      if (closeCase != null && closeCase.booleanValue()) {
        caseRepo.setStatusFor(QuestionnaireServiceImpl.CLOSED, parentCaseId);
        log.debug("parent case marked closed");
        List<Questionnaire> associatedQuestionnaires = questionnaireRepo.findByCaseId(parentCaseId);
        for (Questionnaire questionnaire : associatedQuestionnaires) {
          questionnaireRepo.setResponseDatetimeFor(currentTime, questionnaire.getQuestionnaireId());
        }
        log.debug("all associatedQuestionnaires marked closed");
      }

      String actionType = category.getGeneratedActionType();
      log.debug("actionType = {}", actionType);
      if (actionType != null && !actionType.isEmpty()) {
        ActionDTO actionDTO = new ActionDTO();
        actionDTO.setCaseId(parentCaseId);
        actionDTO.setActionTypeName(actionType);
        actionDTO.setCreatedBy(caseEvent.getCreatedBy());

        log.debug("about to post to the Action SVC with {}", actionDTO);
        actionSvcRestClient.postResource(appConfig.getActionSvc().getActionsPath(), actionDTO, ActionDTO.class);
        log.debug("returned successfully from the post to the Action SVC");
      }
    }

    return result;
  }
}
