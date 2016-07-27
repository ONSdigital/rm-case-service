package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Survey;

/**
 * The Survey Service interface defines all business behaviours for operations
 * on the Survey entity model.
 */
public interface SurveyService extends CTPService {

  /**
   * Returns all Surveys.
   *
   * @return List of Survey entities or empty List
   */
  List<Survey> findSurveys();

  /**
   * Find Survey entity by Survey Id.
   *
   * @param surveyId Survey Id Integer
   * @return Survey entity or null
   */
  Survey findSurveyBySurveyId(Integer surveyId);

}
