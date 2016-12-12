package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.ERRORMSG_CASENOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE1_ACTIONPLANMAPPINGID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE1_TYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_CATEGORY;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_DESC1;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_DESC2;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_DESC3;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_SUBCATEGORY;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE_STATE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CREATEDBY;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CREATEDDATE_VALUE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.NON_EXISTING_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.EXISTING_ID_NO_EVENTS;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.UNCHECKED_EXCEPTION;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.GeneralExceptionMapper;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseGroupServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory;

/**
 * Case Endpoint Unit tests
 */
public final class CaseEndpointUnitTest extends CTPJerseyTest {

  private static final String CASEEVENT_INVALIDJSON =
      "{\"description\":\"a\",\"category\":\"BAD_CAT\",\"createdBy\":\"u\"}";
  private static final String CASEEVENT_VALIDJSON =
      "{\"description\":\"sometest\",\"category\":\"GENERAL_ENQUIRY\",\"createdBy\":\"unittest\"}";

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(CaseEndpoint.class,
        new ServiceFactoryPair [] {
        new ServiceFactoryPair(CategoryService.class, MockCategoryServiceFactory.class),
        new ServiceFactoryPair(CaseService.class, MockCaseServiceFactory.class),
        new ServiceFactoryPair(CaseGroupService.class, MockCaseGroupServiceFactory.class)
        },
        new CaseSvcBeanMapper(),
        new CTPMessageBodyReader<CaseEventDTO>(CaseEventDTO.class));
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdFound() {
    with("http://localhost:9998/cases/%s", CASEID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertStringInBody("$.state", CASE_STATE.name())
        .assertIntegerInBody("$.caseTypeId", CASE1_TYPEID)
        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertIntegerInBody("$.actionPlanMappingId", CASE1_ACTIONPLANMAPPINGID)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdNotFound() {
    with("http://localhost:9998/cases/%s", NON_EXISTING_ID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_ID))
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseIdFound() {
    with("http://localhost:9998/cases/%s/events", CASEID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(3)
        .assertIntegerOccursThroughoutListInBody("$..caseId", CASEID)
        .assertStringListInBody("$..description", CASEEVENT_DESC1, CASEEVENT_DESC2, CASEEVENT_DESC3)
        .assertStringOccursThroughoutListInBody("$..createdBy", CREATEDBY)
        .assertStringOccursThroughoutListInBody("$..createdDateTime", CREATEDDATE_VALUE)
        .assertStringOccursThroughoutListInBody("$..category", CASEEVENT_CATEGORY.name())
        .assertStringOccursThroughoutListInBody("$..subcategory", CASEEVENT_SUBCATEGORY)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseIdFoundButNoEvents() {
    with("http://localhost:9998/cases/%s/events", EXISTING_ID_NO_EVENTS)
        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseIdNotFound() {
    with("http://localhost:9998/cases/%s/events", NON_EXISTING_ID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_ID))
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdUnCheckedException() {
    with("http://localhost:9998/cases/%s", UNCHECKED_EXCEPTION)
        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
        .assertTimestampExists()
        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
        .andClose();
  }

  /**
   * a test providing bad json
   */
  @Test
  public void createCaseEventBadJson() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_INVALIDJSON)
        .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
        .andClose();
  }

  /**
   * a test providing good json
   */
  @Test
  public void createCaseEventGoodJson() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_VALIDJSON)
        .assertResponseCodeIs(HttpStatus.CREATED)
        .assertIntegerInBody("$.caseEventId", 1)
        .assertIntegerInBody("$.caseId", CASEID)
        .assertStringInBody("$.description", CASEEVENT_DESC1)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
        .assertStringInBody("$.category", CASEEVENT_CATEGORY.name())
        .assertStringInBody("$.subCategory", CASEEVENT_SUBCATEGORY)
        .andClose();
  }

  /**
   * a test providing good json
   */
  @Test
  public void createCaseEventCaseNotFound() {
    with("http://localhost:9998/cases/%s/events", NON_EXISTING_ID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_VALIDJSON)
        .assertResponseCodeIs(HttpStatus.CREATED)
        .assertIntegerInBody("$.caseEventId", 1)
        .assertIntegerInBody("$.caseId", CASEID)
        .assertStringInBody("$.description", CASEEVENT_DESC1)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
        .assertStringInBody("$.category", CASEEVENT_CATEGORY.name())
        .assertStringInBody("$.subCategory", CASEEVENT_SUBCATEGORY)
        .andClose();
  }
}
