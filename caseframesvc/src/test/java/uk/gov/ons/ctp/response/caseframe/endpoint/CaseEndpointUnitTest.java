package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.UNCHECKED_EXCEPTION;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE1_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE1_SAMPLEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE1_TYPEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE2_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE2_SAMPLEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE2_TYPEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE3_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE3_SAMPLEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE3_TYPEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEEVENT_CATEGORY;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEEVENT_SUBCATEGORY;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEEVENT_DESC1;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEEVENT_DESC2;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEEVENT_DESC3;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_QUESTIONSET;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_STATUS;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_SURVEYID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CREATEDBY;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CREATEDDATE_VALUE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.NON_EXISTING_ID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.PROVIDED_JSON_INCORRECT;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.QUESTIONNAIREID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.UPRN;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;
import uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory;

/**
 * Case Endpoint Unit tests
 */
public final class CaseEndpointUnitTest extends CTPJerseyTest {

  private static final String CASEEVENT_INVALIDJSON = "{\"some\":\"joke\"}";
  private static final String CASEEVENT_VALIDJSON =
      "{\"description\":\"sometest\",\"category\":\"abc\",\"createdBy\":\"unittest\"}";

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(CaseEndpoint.class, CaseService.class, MockCaseServiceFactory.class, new CaseFrameBeanMapper(),
        new CTPMessageBodyReader<CaseEventDTO>(CaseEventDTO.class));
  }

  /**
   * a test
   */
  @Test
  public void findCasesByUprnFound() {
    with("http://localhost:9998/cases/uprn/%s", UPRN)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(3)
        .assertIntegerOccursThroughoutListInBody("$..uprn", UPRN)
        .assertStringOccursThroughoutListInBody("$..caseStatus", CASE_STATUS)
        .assertIntegerListInBody("$..caseTypeId", CASE1_TYPEID, CASE2_TYPEID, CASE3_TYPEID)
        .assertStringOccursThroughoutListInBody("$..createdDatetime", CREATEDDATE_VALUE)
        .assertStringOccursThroughoutListInBody("$..createdBy", CREATEDBY)
        .assertIntegerListInBody("$..sampleId", CASE1_SAMPLEID, CASE2_SAMPLEID, CASE3_SAMPLEID)
        .assertIntegerListInBody("$..actionPlanId", CASE1_ACTIONPLANID, CASE2_ACTIONPLANID, CASE3_ACTIONPLANID)
        .assertIntegerOccursThroughoutListInBody("$..surveyId", CASE_SURVEYID)
        .assertStringOccursThroughoutListInBody("$..questionSet", CASE_QUESTIONSET)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCasesByUprnNotFound() {
    with("http://localhost:9998/cases/uprn/%s", NON_EXISTING_ID)
        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
        .assertResponseLengthIs(-1)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseByQuestionnaireIdFound() {
    with("http://localhost:9998/cases/questionnaire/%s", QUESTIONNAIREID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.uprn", UPRN)
        .assertStringInBody("$.caseStatus", CASE_STATUS)
        .assertIntegerInBody("$.caseTypeId", CASE1_TYPEID)
        .assertStringInBody("$.createdDatetime", CREATEDDATE_VALUE)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertIntegerInBody("$.sampleId", CASE1_SAMPLEID)
        .assertIntegerInBody("$.actionPlanId", CASE1_ACTIONPLANID)
        .assertIntegerInBody("$.surveyId", CASE_SURVEYID)
        .assertStringInBody("$.questionSet", CASE_QUESTIONSET)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseByQuestionnaireIdNotFound() {
    with("http://localhost:9998/cases/questionnaire/%s", NON_EXISTING_ID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals("Case not found for id %s", NON_EXISTING_ID)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdFound() {
    with("http://localhost:9998/cases/%s", CASEID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.uprn", UPRN)
        .assertStringInBody("$.caseStatus", CASE_STATUS)
        .assertIntegerInBody("$.caseTypeId", CASE1_TYPEID)
        .assertStringInBody("$.createdDatetime", CREATEDDATE_VALUE)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertIntegerInBody("$.sampleId", CASE1_SAMPLEID)
        .assertIntegerInBody("$.actionPlanId", CASE1_ACTIONPLANID)
        .assertIntegerInBody("$.surveyId", CASE_SURVEYID)
        .assertStringInBody("$.questionSet", CASE_QUESTIONSET)
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
        .assertMessageEquals("Case not found for id %s", NON_EXISTING_ID)
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
        .assertStringOccursThroughoutListInBody("$..createdDatetime", CREATEDDATE_VALUE)
        .assertStringOccursThroughoutListInBody("$..category", CASEEVENT_CATEGORY)
        .assertStringOccursThroughoutListInBody("$..subcategory", CASEEVENT_SUBCATEGORY)
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
        .assertMessageEquals("Case not found for id %s", NON_EXISTING_ID)
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
   * a test
   */
  @Test
  public void createCaseEventBadJson() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(CASEEVENT_INVALIDJSON)
        .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
        .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
        .assertTimestampExists()
        .assertMessageEquals(PROVIDED_JSON_INCORRECT)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void createCaseEventGoodJson() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(CASEEVENT_VALIDJSON)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.caseEventId", 1)
        .assertIntegerInBody("$.caseId", CASEID)
        .assertStringInBody("$.description", CASEEVENT_DESC1)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertStringInBody("$.createdDatetime", CREATEDDATE_VALUE)
        .assertStringInBody("$.category", CASEEVENT_CATEGORY)
        .assertStringInBody("$.subCategory", CASEEVENT_SUBCATEGORY)
        .andClose();
  }
}
