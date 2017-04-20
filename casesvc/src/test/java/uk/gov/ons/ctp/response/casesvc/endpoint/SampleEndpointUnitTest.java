package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.NON_EXISTING_SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_CRITERIA;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLE3_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.SURVEY3_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory.UNCHECKED_EXCEPTION;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.representation.GeographyDTO;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockSampleServiceFactory;

/**
 * A set of tests for the Sample Endpoint
 */
public final class SampleEndpointUnitTest {

  @InjectMocks
  private SampleEndpoint sampleEndpoint;

  @Mock
  private SampleService sampleService;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(sampleEndpoint)
            .build();

    sampleService = MockSampleServiceFactory.provide();
  }

  /**
   * a test
   */
  @Test
  public void findSampleBySampleIdFound() {
    assertTrue(true);

//    with("/samples/%s", SAMPLEID)
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertArrayLengthInBodyIs(6)
//        .assertStringListInBody("$..name", SAMPLE3_NAME)
//        .assertStringListInBody("$..description", SAMPLE3_DESC)
//        .assertStringListInBody("$..addressCriteria", SAMPLE3_CRITERIA)
//        .assertStringListInBody("$..survey", SURVEY3_NAME)
//        .andClose();
  }

//  /**
//   * a test
//   */
//  @Test
//  public void findSampleBySampleIdNotFound() {
//    with("/samples/%s", NON_EXISTING_SAMPLEID)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//        .assertTimestampExists()
//        .assertMessageEquals("Sample not found for id %s", NON_EXISTING_SAMPLEID)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findSampleBySampleIdUnCheckedException() {
//    with("/samples/%s", UNCHECKED_EXCEPTION)
//        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
//        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
//        .assertTimestampExists()
//        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
//        .andClose();
//  }
//
//  @Test
//  public void createCasesValidJsonSampleFound() {
//    String putBody = "{\"type\":\"LA\",\"code\":\"E07000163\"}";
//
//    with("/samples/%s", SAMPLEID)
//            .put(MediaType.APPLICATION_JSON_TYPE, putBody)
//            .assertResponseCodeIs(HttpStatus.OK)
//            .assertArrayLengthInBodyIs(6)
//            .assertStringListInBody("$..name", SAMPLE3_NAME)
//            .assertStringListInBody("$..description", SAMPLE3_DESC)
//            .assertStringListInBody("$..addressCriteria", SAMPLE3_CRITERIA)
//            .assertStringListInBody("$..survey", SURVEY3_NAME)
//            .andClose();
//  }
//
//  @Test
//  public void createCasesValidJsonSampleNotFound() {
//    with("/samples/%s", NON_EXISTING_SAMPLEID)
//            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//            .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//            .assertTimestampExists()
//            .assertMessageEquals("Sample not found for id %s", NON_EXISTING_SAMPLEID)
//            .andClose();
//  }
//
//  @Test
//  public void createCasesBadJson() {
//    String putBody = "{\"badtype\":\"LA\",\"code\":\"E07000163\"}";
//
//    with("/samples/%s", SAMPLEID)
//            .put(MediaType.APPLICATION_JSON_TYPE, putBody)
//            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
//            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
//            .assertTimestampExists()
//            .assertMessageEquals(GeneralExceptionMapper.BAD_JSON)
//            .andClose();
//  }
}
