package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN1_DESC;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN1_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN1_RULES;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN2_DESC;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN2_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN2_RULES;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN3_DESC;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN3_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLAN3_RULES;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.NON_EXISTING_ACTIONPLANID;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory.UNCHECKED_EXCEPTION;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.service.ActionPlanService;
import uk.gov.ons.ctp.response.caseframe.utility.MockActionPlanServiceFactory;

/**
 * Unit tests for ActionPlan endpoint
 */
public class ActionPlanEndpointUnitTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(ActionPlanEndpoint.class, ActionPlanService.class, MockActionPlanServiceFactory.class, new CaseFrameBeanMapper()); 
  }

  @Test
  public void findActionPlansFound() {
    with("http://localhost:9998/actionplans")
      .assertResponseCodeIs(HttpStatus.OK)
      .assertArrayLengthInBodyIs(3)
      .assertStringListInBody("$..actionPlanName", ACTIONPLAN1_NAME, ACTIONPLAN2_NAME, ACTIONPLAN3_NAME)
      .assertStringListInBody("$..description", ACTIONPLAN1_DESC, ACTIONPLAN2_DESC, ACTIONPLAN3_DESC)
      .assertStringListInBody("$..rules", ACTIONPLAN1_RULES, ACTIONPLAN2_RULES, ACTIONPLAN3_RULES)
      .andClose();
  }

  @Test
  public void findActionPlanFound() {
    with("http://localhost:9998/actionplans/%s", ACTIONPLANID)
      .assertResponseCodeIs(HttpStatus.OK)
      .assertIntegerInBody("$.actionPlanId", 3)
      .assertStringInBody("$.actionPlanName", ACTIONPLAN3_NAME)
      .assertStringInBody("$.description", ACTIONPLAN3_DESC)
      .assertStringInBody("$.rules", ACTIONPLAN3_RULES)
      .andClose();
  }

  @Test
  public void findActionPlanNotFound() {
    with("http://localhost:9998/actionplans/%s", NON_EXISTING_ACTIONPLANID)
      .assertResponseCodeIs(HttpStatus.NOT_FOUND)
      .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
      .assertTimestampExists()
      .assertMessageEquals("ActionPlan not found for id %s", NON_EXISTING_ACTIONPLANID)
      .andClose();
  }

  @Test
  public void findActionPlanUnCheckedException() {
    with("http://localhost:9998/actionplans/%s", UNCHECKED_EXCEPTION)
      .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
      .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
      .assertTimestampExists()
      .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
      .andClose();
  }
}