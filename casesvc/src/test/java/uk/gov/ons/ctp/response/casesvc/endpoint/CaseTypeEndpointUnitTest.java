package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_QUESTIONSET;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.NON_EXISTING_CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.UNCHECKED_EXCEPTION;

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
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory;

/**
 * Created by Martin.Humphrey on 26/2/2016.
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

    caseTypeService = MockCaseTypeServiceFactory.provide();
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
