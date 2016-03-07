package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;
import uk.gov.ons.ctp.response.caseframe.representation.SurveyDTO;
import uk.gov.ons.ctp.response.caseframe.service.SurveyService;

/**
 * The REST endpoint controller for CaseFrame Surveys
 */
@Path("/surveys")
@Produces({ "application/json" })
@Slf4j
public final class SurveyEndpoint implements CTPEndpoint {

  @Inject
  private SurveyService surveyService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve all surveys
   * @return the list of surveys
   */
  @GET
  @Path("/")
  public List<SurveyDTO> findSurveys() {
    log.debug("Entering findSurveys...");
    List<Survey> surveys = surveyService.findSurveys();
    List<SurveyDTO> surveyDTOs = mapperFacade.mapAsList(surveys, SurveyDTO.class);
    return CollectionUtils.isEmpty(surveyDTOs) ? null : surveyDTOs;
  }

  /**
   * the GET endpoint to retrieve a survey by its id
   * @param surveyId the id of the survey to fetch
   * @return the survey representation found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{surveyid}")
  public SurveyDTO findSurveyBySurveyId(@PathParam("surveyid") final Integer surveyId) throws CTPException {
    log.debug("Entering findSurveyBySurveyId with {}", surveyId);
    Survey survey = surveyService.findSurveyBySurveyId(surveyId);
    if (survey == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Survey not found for id %s", surveyId);
    }
    return mapperFacade.map(survey, SurveyDTO.class);
  }
}
