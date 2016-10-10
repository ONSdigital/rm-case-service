package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE1_ACTIONPLANID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE1_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE1_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE1_QUESTIONSET;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE2_ACTIONPLANID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE2_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE2_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE2_QUESTIONSET;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_ACTIONPLANID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPE3_QUESTIONSET;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.NON_EXISTING_CASETYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory.UNCHECKED_EXCEPTION;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseTypeEndpoint;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory;

/**
 * Created by Martin.Humphrey on 26/2/2016.
 */
public final class CaseTypeEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(CaseTypeEndpoint.class, CaseTypeService.class, MockCaseTypeServiceFactory.class,
        new CaseSvcBeanMapper());
  }

  @Test
  public void removeThisDummyTestOnceAllTestsReinstated() {
  }
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseTypesFound() {
//    with("http://localhost:9998/casetypes")
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertArrayLengthInBodyIs(3)
//        .assertStringListInBody("$..name", CASETYPE1_NAME, CASETYPE2_NAME, CASETYPE3_NAME)
//        .assertStringListInBody("$..description", CASETYPE1_DESC, CASETYPE2_DESC, CASETYPE3_DESC)
//        .assertIntegerListInBody("$..actionPlanId", CASETYPE1_ACTIONPLANID, CASETYPE2_ACTIONPLANID,
//            CASETYPE3_ACTIONPLANID)
//        .assertStringListInBody("$..questionSet", CASETYPE1_QUESTIONSET, CASETYPE2_QUESTIONSET, CASETYPE3_QUESTIONSET)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseTypeByCaseTypeIdFound() {
//    with("http://localhost:9998/casetypes/%s", CASETYPEID)
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertIntegerInBody("$.caseTypeId", 3)
//        .assertStringInBody("$.name", CASETYPE3_NAME)
//        .assertStringInBody("$.description", CASETYPE3_DESC)
//        .assertIntegerInBody("$.actionPlanId", CASETYPE3_ACTIONPLANID)
//        .assertStringInBody("$.questionSet", CASETYPE3_QUESTIONSET)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseTypeByCaseTypeIdNotFound() {
//    with("http://localhost:9998/casetypes/%s", NON_EXISTING_CASETYPEID)
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
//    with("http://localhost:9998/casetypes/%s", UNCHECKED_EXCEPTION)
//        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
//        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
//        .assertTimestampExists()
//        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
//        .andClose();
//  }
//
}
