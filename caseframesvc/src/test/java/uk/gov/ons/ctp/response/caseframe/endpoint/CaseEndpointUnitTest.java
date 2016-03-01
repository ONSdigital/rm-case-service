package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE1_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE1_SAMPLEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE1_TYPEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE2_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE2_SAMPLEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE2_TYPEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE3_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE3_SAMPLEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE3_TYPEID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_CREATEDBY;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_CREATEDDATE_VALUE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_QUESTIONSET;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_STATUS;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASE_SURVEYID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.UPRN;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.NON_EXISTING_ID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory.CASEID;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;
import uk.gov.ons.ctp.response.caseframe.utility.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.utility.MockCaseServiceFactory;

public class CaseEndpointUnitTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(CaseEndpoint.class, CaseService.class, MockCaseServiceFactory.class); 
  }

  @Test
  public void findCasesByUprnFound() {
    with("http://localhost:9998/cases/uprn/%s", UPRN)
    .assertResponseCodeIs(HttpStatus.OK)
    .assertArrayLengthInBodyIs(3)
    .assertIntegerOccursThroughoutListInBody("$..uprn",  UPRN)
    .assertStringOccursThroughoutListInBody("$..caseStatus", CASE_STATUS)
    .assertIntegerListInBody("$..caseTypeId", CASE1_TYPEID, CASE2_TYPEID, CASE3_TYPEID)
    .assertStringOccursThroughoutListInBody("$..createdDatetime", CASE_CREATEDDATE_VALUE)
    .assertStringOccursThroughoutListInBody("$..createdBy", CASE_CREATEDBY)
    .assertIntegerListInBody("$..sampleId", CASE1_SAMPLEID, CASE2_SAMPLEID, CASE3_SAMPLEID)
    .assertIntegerListInBody("$..actionPlanId", CASE1_ACTIONPLANID, CASE2_ACTIONPLANID, CASE3_ACTIONPLANID)
    .assertIntegerOccursThroughoutListInBody("$..surveyId", CASE_SURVEYID)
    .assertStringOccursThroughoutListInBody("$..questionSet", CASE_QUESTIONSET)
    .andClose();
  }

  @Test
  public void findCasesByUprnNotFound() {
    with("http://localhost:9998/cases/uprn/%s", NON_EXISTING_ID)
      .assertResponseCodeIs(HttpStatus.NO_CONTENT)
      .assertResponseLengthIs(-1)
      .andClose();
  }
  @Test
  public void findCaseByCaseIdFound() {
    with("http://localhost:9998/cases/%s", CASEID)
    .assertResponseCodeIs(HttpStatus.OK)
    .assertIntegerInBody("$.uprn",  UPRN)
    .assertStringInBody("$.caseStatus", CASE_STATUS)
    .assertIntegerInBody("$.caseTypeId", CASE1_TYPEID)
    .assertStringInBody("$.createdDatetime", CASE_CREATEDDATE_VALUE)
    .assertStringInBody("$.createdBy", CASE_CREATEDBY)
    .assertIntegerInBody("$.sampleId", CASE1_SAMPLEID)
    .assertIntegerInBody("$.actionPlanId", CASE1_ACTIONPLANID)
    .assertIntegerInBody("$.surveyId", CASE_SURVEYID)
    .assertStringInBody("$.questionSet", CASE_QUESTIONSET)
    .andClose();
  }

  @Test
  public void findCaseByCaseIdNotFound() {
    with("http://localhost:9998/cases/%s", NON_EXISTING_ID)
    .assertResponseCodeIs(HttpStatus.NOT_FOUND)
    .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
    .assertTimestampExists()
    .assertMessageEquals("Case not found for id %s", NON_EXISTING_ID)
    .andClose();
  }
} 
