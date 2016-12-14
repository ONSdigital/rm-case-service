package uk.gov.ons.ctp.response.casesvc.endpoint;


import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.CASE_GROUP_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.NON_EXISTENT_CASE_GROUP_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.SAMPLE_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_NON_EXISTING_UPRN;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_UPRN_NO_CASEGROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_UPRN;
import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory;

/**
 */
public final class CaseGroupEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(CaseGroupEndpoint.class,
        new ServiceFactoryPair[] {
            new ServiceFactoryPair(CaseGroupService.class, MockCaseGroupServiceFactory.class),
            new ServiceFactoryPair(AddressService.class, MockAddressServiceFactory.class)
        },
        new CaseSvcBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupByCaseGroupIdFound() {
    with("http://localhost:9998/casegroups/%s", CASE_GROUP_ID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.caseGroupId", CASE_GROUP_ID)
        .assertIntegerInBody("$.sampleId", SAMPLE_ID)
        .assertLongInBody("$.uprn", ADDRESS_UPRN)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupByCaseGroupIdNotFound() {
    with("http://localhost:9998/casegroups/%s", NON_EXISTENT_CASE_GROUP_ID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals("CaseGroup not found for casegroup id %s", NON_EXISTENT_CASE_GROUP_ID)
        .andClose();
  }
  
  /**
   * a test 
   */
  @Test
  public void findCaseGroupByUprnNotFound() {
    with("http://localhost:9998/casegroups/uprn/%s", ADDRESS_NON_EXISTING_UPRN)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals(CaseGroupEndpoint.ERRORMSG_ADDRESSNOTFOUND + " UPRN %s", ADDRESS_NON_EXISTING_UPRN)
        .andClose();
  }
  
  /**
   * a test
   */
  @Test
  public void findCaseGroupsByUprnFound() {
    with("http://localhost:9998/casegroups/uprn/%s", ADDRESS_UPRN)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(1)
        .assertIntegerListInBody("$..caseGroupId", CASE_GROUP_ID)
        .assertIntegerListInBody("$..sampleId", SAMPLE_ID)
        .assertLongOccursThroughoutListInBody("$..uprn", ADDRESS_UPRN)
        .andClose();
  }
  
  /**
   * a test
   */
  @Test
  public void findCaseGroupsByUprnFoundButNoCaseGroups() {
    with("http://localhost:9998/casegroups/uprn/%s", ADDRESS_UPRN_NO_CASEGROUP)
        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
        .andClose();
  }
  
}
