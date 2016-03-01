package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;
import uk.gov.ons.ctp.response.caseframe.domain.repository.SurveyRepository;
import uk.gov.ons.ctp.response.caseframe.service.SurveyService;

/**
 * An implementation of the SurveyService using JPA Repository class(es)
 * The business logic for the application should reside here.
 */
@Named
@Slf4j
@Data
public class SurveyServiceImpl implements SurveyService {

  @Inject
  private SurveyRepository surveyRepo;

  public List<Survey> findSurveys() {
    log.debug("Entering findSurveys");
    return surveyRepo.findAll();
  }

  public Survey findSurveyBySurveyId(Integer surveyId) {
    log.debug("Entering findSurveyBySurveyId with {}", surveyId);
    return surveyRepo.findOne(surveyId);
  }

}
