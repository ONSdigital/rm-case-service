package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;

/**
 * The Questionnaire Service interface defines all business behaviours for
 * operations on the Questionnaire entity model.
 */
public interface QuestionnaireService extends CTPService {

  /**
   * Find Questionnaire entity by Internet Access Code.
   *
   * @param iac Unique IAC String
   * @return Questionnaire object or null
   */
  Questionnaire findQuestionnaireByIac(String iac);

  /**
   * Find Questionnaire entities associated with a Case.
   *
   * @param caseId Case Id Integer
   * @return List of Questionnaire entities or empty List
   */
  List<Questionnaire> findQuestionnairesByCaseId(Integer caseId);

  /**
   * Update a Questionnaire and Case object to record a response has been
   * received in the Survey Data Exchange.
   *
   * @param questionnaireid Unique Questionnaire Id
   * @return Updated Questionnaire object
   */
  Questionnaire recordResponse(Integer questionnaireid);

}
