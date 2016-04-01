package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.representation.ActionDTO;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

/**
 * A CaseService implementation which encapsulates all business logic operating
 * on the Case entity model.
 */
@Named
@Slf4j
public final class CaseServiceImpl implements CaseService {

  private static final int TRANSACTION_TIMEOUT = 30;

  @Value("${actionsvc.actionsurl}")
  private String actionSvcUrl;

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

  // TODO finish off refactoring
  private final RestTemplate restTemplate;

  public CaseServiceImpl() {
    restTemplate = new RestTemplate();
  }

  @Override
  public final List<Case> findCasesByUprn(final Integer uprn) {
    log.debug("Entering findCasesByUprn with uprn {}", uprn);
    return caseRepo.findByUprn(uprn);
  }

  @Override
  public final Case findCaseByQuestionnaireId(final Integer qid) {
    log.debug("Entering findCaseByQuestionnaireId");
    Questionnaire questionnaire = questionnaireRepo.findByQuestionnaireId(qid);
    if (questionnaire == null) {
      return null;
    }
    return caseRepo.findOne(questionnaire.getCaseId());
  }

  @Override
  public final Case findCaseByCaseId(final Integer caseId) {
    log.debug("Entering findCaseByCaseId");
    return caseRepo.findOne(caseId);
  }

  @Override
  public final List<CaseEvent> findCaseEventsByCaseId(final Integer caseId) {
    log.debug("Entering findCaseEventsByCaseId");
    return caseEventRepository.findByCaseIdOrderByCreatedDateTimeDesc(caseId);
  }

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  @Override
  public final CaseEvent createCaseEvent(final CaseEvent caseEvent) {
    log.debug("Entering createCaseEvent");
    Integer parentCaseId = caseEvent.getCaseId();
    Case parentCase = caseRepo.findOne(parentCaseId);
    log.debug("parentCase = {}", parentCase);
    if (parentCase != null) {
      Timestamp currentTime = new Timestamp(System.currentTimeMillis());
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
        restTemplate.postForObject(actionSvcUrl, actionDTO, ActionDTO.class);
        log.debug("returned successfully from the post to the Action SVC");
      }

      caseEvent.setCreatedDateTime(currentTime);
      log.debug("about to create the caseEvent for {}", caseEvent);
      return caseEventRepository.save(caseEvent);
    } else {
      return null;
    }
  }
}
