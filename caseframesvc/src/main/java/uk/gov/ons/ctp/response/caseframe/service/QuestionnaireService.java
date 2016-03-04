package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;

/**
 * The interface defining the function of the Region service. The application
 * business logic should reside in it's implementation
 */
public interface QuestionnaireService extends CTPService {

  Questionnaire findQuestionnaireByIac(String iac);

  List<Questionnaire> findQuestionnairesByCaseId(Integer caseId);

  int updateResponseTime(Integer questionnaireid);

  int closeParentCase(Integer questionnaireid);

}