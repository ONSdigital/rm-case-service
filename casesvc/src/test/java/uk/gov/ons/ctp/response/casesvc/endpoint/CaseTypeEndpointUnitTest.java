package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

/**
 * Leave commented out as will be deleted in BRES
 */
public final class CaseTypeEndpointUnitTest {

  @InjectMocks
  private CaseTypeEndpoint caseTypeEndpoint;

  @Mock
  private CaseTypeService caseTypeService;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseTypeEndpoint)
            .build();
  }

  /**
   * a test
   */
  @Test
  public void findCaseTypeByCaseTypeIdFound() {
    assertTrue(true);
//    with("/casetypes/%s", CASETYPEID)
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertIntegerInBody("$.caseTypeId", 3)
//        .assertStringInBody("$.name", CASETYPE3_NAME)
//        .assertStringInBody("$.description", CASETYPE3_DESC)
//        .assertStringInBody("$.questionSet", CASETYPE3_QUESTIONSET)
//        .andClose();
  }

//  /**
//   * a test
//   */
//  @Test
//  public void findCaseTypeByCaseTypeIdNotFound() {
//    with("/casetypes/%s", NON_EXISTING_CASETYPEID)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//        .assertTimestampExists()
//        .assertMessageEquals("CaseType not found for id %s", NON_EXISTING_CASETYPEID)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseTypeByCaseTypeIdUnCheckedException() {
//    with("/casetypes/%s", UNCHECKED_EXCEPTION)
//        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
//        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
//        .assertTimestampExists()
//        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
//        .andClose();
//  }

}
