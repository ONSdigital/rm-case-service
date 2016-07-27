package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Survey;
import uk.gov.ons.ctp.response.casesvc.domain.repository.SurveyRepository;
import uk.gov.ons.ctp.response.casesvc.service.SurveyService;

/**
 * An implementation of the SurveyService using JPA Repository class(es) The
 * business logic for the application should reside here.
 */
@Named
@Slf4j
@Data
public final class SurveyServiceImpl implements SurveyService {

  @Inject
  private SurveyRepository surveyRepo;

  @Override
  public List<Survey> findSurveys() {
    log.debug("Entering findSurveys");
    return surveyRepo.findAll();
  }

  @Override
  public Survey findSurveyBySurveyId(final Integer surveyId) {
    log.debug("Entering findSurveyBySurveyId with {}", surveyId);
    return surveyRepo.findOne(surveyId);
  }

}
