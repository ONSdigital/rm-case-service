package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.CASE_GROUP_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.NON_EXISTENT_CASE_GROUP_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.SAMPLE_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory.UPRN;

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
        .assertLongInBody("$.uprn", UPRN)
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
  public void findCaseGroupByUprn() {
    with("http://localhost:9998/casegroups/uprn/%s", UPRN)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertArrayLengthInBodyIs(1)
        .assertIntegerOccursThroughoutListInBody("$..caseGroupId", CASE_GROUP_ID)
        .assertIntegerOccursThroughoutListInBody("$..sampleId", SAMPLE_ID)
        .assertLongOccursThroughoutListInBody("$..uprn", UPRN);
  }
}
