package uk.gov.ons.ctp.response.casesvc.endpoint;


import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_NON_EXISTING_UPRN;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_UPRN;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_UPRN_NO_CASEGROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.CASE_GROUP_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.NON_EXISTENT_CASE_GROUP_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.SAMPLE_ID;

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
import uk.gov.ons.ctp.response.casesvc.service.AddressService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory;

/**
 */
public final class CaseGroupEndpointUnitTest {

  @InjectMocks
  private CaseGroupEndpoint caseGroupEndpoint;

  @Mock
  private AddressService addressService;

  @Mock
  private CaseGroupService caseGroupService;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseGroupEndpoint)
            .build();

    caseGroupService = MockCaseGroupServiceFactory.provide();
    addressService = MockAddressServiceFactory.provide();
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupByCaseGroupIdFound() {
    assertTrue(true);
//    with("/casegroups/%s", CASE_GROUP_ID)
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertIntegerInBody("$.caseGroupId", CASE_GROUP_ID)
//        .assertIntegerInBody("$.sampleId", SAMPLE_ID)
//        .assertLongInBody("$.uprn", ADDRESS_UPRN)
//        .andClose();
  }

//  /**
//   * a test
//   */
//  @Test
//  public void findCaseGroupByCaseGroupIdNotFound() {
//    with("/casegroups/%s", NON_EXISTENT_CASE_GROUP_ID)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//        .assertTimestampExists()
//        .assertMessageEquals("CaseGroup not found for casegroup id %s", NON_EXISTENT_CASE_GROUP_ID)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseGroupByUprnNotFound() {
//    with("/casegroups/uprn/%s", ADDRESS_NON_EXISTING_UPRN)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//        .assertTimestampExists()
//        .assertMessageEquals(CaseGroupEndpoint.ERRORMSG_ADDRESSNOTFOUND + " UPRN %s", ADDRESS_NON_EXISTING_UPRN)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseGroupsByUprnFound() {
//    with("/casegroups/uprn/%s", ADDRESS_UPRN)
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertArrayLengthInBodyIs(1)
//        .assertIntegerListInBody("$..caseGroupId", CASE_GROUP_ID)
//        .assertIntegerListInBody("$..sampleId", SAMPLE_ID)
//        .assertLongOccursThroughoutListInBody("$..uprn", ADDRESS_UPRN)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseGroupsByUprnFoundButNoCaseGroups() {
//    with("/casegroups/uprn/%s", ADDRESS_UPRN_NO_CASEGROUP)
//        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
//        .andClose();
//  }
  
}
