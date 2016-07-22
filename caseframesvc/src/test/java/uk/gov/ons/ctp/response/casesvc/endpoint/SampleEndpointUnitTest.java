package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.NON_EXISTING_SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE1_CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE1_CRITERIA;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE1_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE1_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE2_CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE2_CRITERIA;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE2_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE2_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_CRITERIA;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SURVEYID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.UNCHECKED_EXCEPTION;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSurveyServiceFactory.NON_EXISTING_SURVEYID;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.GeneralExceptionMapper;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.casesvc.representation.GeographyDTO;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;
import uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory;

/**
 * A set of tests for the Sample Endpoint
 */
public final class SampleEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(SampleEndpoint.class, SampleService.class, MockSampleServiceFactory.class,
        new CaseFrameBeanMapper(), new CTPMessageBodyReader<GeographyDTO>(GeographyDTO.class));
  }

  /**
   * a test
   */
  @Test
  public void findSamplesFound() {
    with("http://localhost:9998/samples")
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(3)
        .assertStringListInBody("$..name", SAMPLE1_NAME, SAMPLE2_NAME, SAMPLE3_NAME)
        .assertStringListInBody("$..description", SAMPLE1_DESC, SAMPLE2_DESC, SAMPLE3_DESC)
        .assertStringListInBody("$..addressCriteria", SAMPLE1_CRITERIA, SAMPLE2_CRITERIA, SAMPLE3_CRITERIA)
        .assertIntegerListInBody("$..caseTypeId", SAMPLE1_CASETYPEID, SAMPLE2_CASETYPEID, SAMPLE3_CASETYPEID)
        .assertIntegerListInBody("$..surveyId", SURVEYID, SURVEYID, SURVEYID)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findSampleBySampleIdFound() {
    with("http://localhost:9998/samples/%s", SAMPLEID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(6)
        .assertStringListInBody("$..name", SAMPLE3_NAME)
        .assertStringListInBody("$..description", SAMPLE3_DESC)
        .assertStringListInBody("$..addressCriteria", SAMPLE3_CRITERIA)
        .assertIntegerListInBody("$..caseTypeId", SAMPLE3_CASETYPEID)
        .assertIntegerListInBody("$..surveyId", SURVEYID)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findSampleBySampleIdNotFound() {
    with("http://localhost:9998/samples/%s", NON_EXISTING_SAMPLEID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals("Sample not found for id %s", NON_EXISTING_SURVEYID)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findSampleBySampleIdUnCheckedException() {
    with("http://localhost:9998/samples/%s", UNCHECKED_EXCEPTION)
        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
        .assertTimestampExists()
        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void createCasesValidJson() {
    String putBody = "{\"type\":\"LA\",\"code\":\"E07000163\"}";

    with("http://localhost:9998/samples/%s", SAMPLEID)
        .put(MediaType.APPLICATION_JSON_TYPE, putBody)
        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
        .assertEmptyResponse()
        .andClose();
  }

  @Test
  public void createCasesBadJson() {
    String putBody = "{\"badtype\":\"LA\",\"code\":\"E07000163\"}";

    with("http://localhost:9998/samples/%s", SAMPLEID)
            .put(MediaType.APPLICATION_JSON_TYPE, putBody)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
            .assertTimestampExists()
            .assertMessageEquals(GeneralExceptionMapper.BAD_JSON)
            .andClose();
  }
}
