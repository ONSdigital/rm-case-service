package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.NON_EXISTING_SURVEYID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY1_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY1_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY2_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY2_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY3_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY3_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY4_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEY4_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.SURVEYID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.UNCHECKED_EXCEPTION;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.endpoint.SurveyEndpoint;
import uk.gov.ons.ctp.response.casesvc.service.SurveyService;
import uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory;

/**
 * Unit tests for the SurveyEndpoint
 */
public final class SurveyEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(SurveyEndpoint.class, SurveyService.class, MockSurveyServiceFactory.class,
        new CaseSvcBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findSurveysFound() {
    with("http://localhost:9998/surveys")
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(4)
        .assertStringListInBody("$..name", SURVEY1_NAME, SURVEY2_NAME, SURVEY3_NAME, SURVEY4_NAME)
        .assertStringListInBody("$..description", SURVEY1_DESC, SURVEY2_DESC, SURVEY3_DESC, SURVEY4_DESC)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findSurveyBySurveyIdFound() {
    with("http://localhost:9998/surveys/%s", SURVEYID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.surveyid", 4)
        .assertStringInBody("$.name", SURVEY4_NAME)
        .assertStringInBody("$.description", SURVEY4_DESC)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findSurveyBySurveyIdNotFound() {
    with("http://localhost:9998/surveys/%s", NON_EXISTING_SURVEYID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals("Survey not found for id %s", NON_EXISTING_SURVEYID)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findSurveyBySurveyIdUnCheckedException() {
    with("http://localhost:9998/surveys/%s", UNCHECKED_EXCEPTION)
        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
        .assertTimestampExists()
        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
        .andClose();
  }

}
