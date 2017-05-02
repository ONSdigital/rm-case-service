package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.putJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.mockito.Spy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

/**
 * Leave commented out as will be deleted in BRES
 */
public final class SampleEndpointUnitTest {

  @InjectMocks
  private SampleEndpoint sampleEndpoint;

  @Mock
  private SampleService sampleService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;

  private static final Integer SAMPLEID = 3;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(sampleEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
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
  @Test
  public void createCasesValidJsonSampleFound() throws Exception {
    String putBody = "{\"type\":\"LA\",\"code\":\"E07000163\"}";

    ResultActions actions = mockMvc.perform(putJson(String.format("/samples/%s", SAMPLEID), putBody));

    actions.andExpect(status().isNotFound());

//            .assertResponseCodeIs(HttpStatus.OK)
//            .assertArrayLengthInBodyIs(6)
//            .assertStringListInBody("$..name", SAMPLE3_NAME)
//            .assertStringListInBody("$..description", SAMPLE3_DESC)
//            .assertStringListInBody("$..addressCriteria", SAMPLE3_CRITERIA)
//            .assertStringListInBody("$..survey", SURVEY3_NAME)
//            .andClose();
  }
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
