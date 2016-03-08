package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.annotation.Propagation;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.service.QuestionnaireService;

/**
 * A QuestionnaireService implementation which encapsulates all business logic
 * operating on the Questionnaire entity model.
 */
@Named
@Slf4j
@Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
public final class QuestionnaireServiceImpl implements QuestionnaireService {

  public static final String OPERATION_FAILED = "Response operation failed for questionnaireid";
  public static final String CLOSED = "CLOSED";
  static final int TRANSACTION_TIMEOUT = 30;

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
   * Find Questionnaire entity by Internet Access Code.
   *
   * @param IAC
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
   * @param Case Id
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
   * @param questionnaireId id of questionnaire
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
    int nbOfUpdatedCases = caseRepo.setStatusFor(CLOSED, questionnaire.getCaseId());
    if (!(nbOfUpdatedQuestionnaires == 1 && nbOfUpdatedCases == 1)) {
      log.error("{} {} - nbOfUpdatedQuestionnaires = {} - nbOfUpdatedCases = {}", OPERATION_FAILED, questionnaireId,
          nbOfUpdatedQuestionnaires, nbOfUpdatedCases);
      return null;
    }
    return questionnaire;
  }
}
