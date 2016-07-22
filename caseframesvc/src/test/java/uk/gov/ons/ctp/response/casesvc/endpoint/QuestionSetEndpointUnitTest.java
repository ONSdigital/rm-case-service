package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.NON_EXISTING_QUESTIONSETNAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.QUESTIONSET1_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.QUESTIONSET1_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.QUESTIONSET2_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.QUESTIONSET2_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.QUESTIONSETNAME;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory.UNCHECKED_EXCEPTION;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.casesvc.endpoint.QuestionSetEndpoint;
import uk.gov.ons.ctp.response.casesvc.service.QuestionSetService;
import uk.gov.ons.ctp.response.casesvc.utility.MockQuestionSetServiceFactory;

/**
 * QuestionSet Endpoint Unit Tests using CTPJerseyTest DSL framework
 */
public final class QuestionSetEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(QuestionSetEndpoint.class, QuestionSetService.class, MockQuestionSetServiceFactory.class,
        new CaseFrameBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findQuestionSetsFound() {
    with("http://localhost:9998/questionsets")
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(2)
        .assertStringListInBody("$..questionSet", QUESTIONSET1_NAME, QUESTIONSET2_NAME)
        .assertStringListInBody("$..description", QUESTIONSET1_DESC, QUESTIONSET2_DESC)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findQuestionSetByQuestionSetFound() {
    with("http://localhost:9998/questionsets/%s", QUESTIONSETNAME)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(2)
        .assertStringInBody("$.questionSet", QUESTIONSET1_NAME)
        .assertStringInBody("$.description", QUESTIONSET1_DESC)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findQuestionSetByQuestionSetNotFound() {
    with("http://localhost:9998/questionsets/%s", NON_EXISTING_QUESTIONSETNAME)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals("QuestionSet not found for id %s", NON_EXISTING_QUESTIONSETNAME)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findQuestionSetBQuestionSetUnCheckedException() {
    with("http://localhost:9998/questionsets/%s", UNCHECKED_EXCEPTION)
        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
        .assertTimestampExists()
        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
        .andClose();
  }
}
