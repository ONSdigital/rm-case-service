package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.validation.Valid;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;
import uk.gov.ons.ctp.response.caseframe.service.QuestionnaireService;

/**
 * A QuestionnaireService implementation which encapsulates all business logic
 * operating on the Questionnaire entity model.
 */
@Named
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public final class QuestionnaireServiceImpl implements QuestionnaireService {

  /**
   * Text associated with failure of Response data received operation
   */
  public static final String OPERATION_FAILED = "Response operation failed for questionnaireid";
  /**
   * Case status set on Response data being received
   */
  public static final String CLOSED = "CLOSED";
  private static final int TRANSACTION_TIMEOUT = 30;
  
  /**
   * Questionnaire category name as expected in the Category table
   */
  private static final String QUESTIONNAIRE_CATEGORY = "QuestionnareResponse";

  /**
   * Spring Data Repository for Case entities.
   */
  @Inject
  private CaseRepository caseRepo;

  /**
   * Spring Data Repository for Case service.
   */
  @Inject
  private CaseService caseService;
  
  @Inject
  private MapperFacade mapperFacade;
  
  /**
   * Spring Data Repository for Questionnaire Entities.
   */
  @Inject
  private QuestionnaireRepository questionnaireRepo;

  /**
   * Find Questionnaire entity by Internet Access Code.
   *
   * @param iac Unique Internet Access Code
   * @return Questionnaire object or null
   */
  @Override
  public Questionnaire findQuestionnaireByIac(final String iac) {
    log.debug("Entering findQuestionnaireByIac with {}", iac);
    return questionnaireRepo.findByIac(iac);
  }

  /**
   * Find Questionnaire entities associated with a Case.
   *
   * @param caseId Unique Case Id
   * @return List of Questionnaire entities or empty List
   */
  @Override
  public List<Questionnaire> findQuestionnairesByCaseId(final Integer caseId) {
    log.debug("Entering findQuestionnairesByCaseId with {}", caseId);
    return questionnaireRepo.findByCaseId(caseId);
  }

  /**
   * Update a Questionnaire and Case object to record a response has been
   * received in the Survey Data Exchange.
   *
   * @param questionnaireId Unique Id of questionnaire
   * @return Updated Questionnaire object or null
   */
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, timeout = TRANSACTION_TIMEOUT)
  public Questionnaire recordResponse(final Integer questionnaireId) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    Questionnaire questionnaire = questionnaireRepo.findOne(questionnaireId);
    if (questionnaire == null) {
      // Questionnaire does not exist to record response
      return null;
    }
    int nbOfUpdatedQuestionnaires = questionnaireRepo.setResponseDatetimeFor(currentTime, questionnaireId);
    
    // CTPA-295 development  - create CaseEvent for cancelling Action and closing Case
    int caseId = questionnaire.getCaseId();
    CaseEventDTO caseEventDTO = new CaseEventDTO();
    caseEventDTO.setCaseId(caseId);
    CaseEvent caseEvent = mapperFacade.map(caseEventDTO, CaseEvent.class);
    caseEvent.setCategory(QUESTIONNAIRE_CATEGORY);
    caseService.createCaseEvent(caseEvent);
    //
    
//    int nbOfUpdatedCases = caseRepo.setStatusFor(CLOSED, questionnaire.getCaseId());
//    if (!(nbOfUpdatedQuestionnaires == 1 && nbOfUpdatedCases == 1)) {
//      log.error("{} {} - nbOfUpdatedQuestionnaires = {} - nbOfUpdatedCases = {}", OPERATION_FAILED, questionnaireId,
//          nbOfUpdatedQuestionnaires, nbOfUpdatedCases);
//      return null;
//    }
    
    
    return questionnaire;
  }
}
