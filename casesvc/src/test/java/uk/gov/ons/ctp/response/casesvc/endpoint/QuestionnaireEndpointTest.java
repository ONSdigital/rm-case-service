package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.endpoint.QuestionnaireEndpoint.ERRORMSG_QUESTIONNAIRENOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_CASEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_CASEID_NOT_FOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_IAC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_IAC_NOT_FOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_ID_1;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_ID_2;
import static uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory.QUESTIONNAIRE_ID_NOT_FOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.QuestionnaireBuilder.QUESTIONNAIRE_SET;
import static uk.gov.ons.ctp.response.casesvc.utility.QuestionnaireBuilder.QUESTIONNAIRE_STATUS;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.service.QuestionnaireService;
import uk.gov.ons.ctp.response.casesvc.utility.MockQuestionnaireServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.QuestionnaireBuilder;

/**
 * Created by philippe.brossier on 2/26/16.
 */
public final class QuestionnaireEndpointTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(QuestionnaireEndpoint.class, QuestionnaireService.class, MockQuestionnaireServiceFactory.class,
        new CaseSvcBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findQuestionnaireByIac() {
    with("http://localhost:9998/questionnaires/iac/%s", QUESTIONNAIRE_IAC)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.questionnaireId", QUESTIONNAIRE_ID_1)
        .assertIntegerInBody("$.caseId", QUESTIONNAIRE_CASEID)
        .assertStringInBody("$.iac", QUESTIONNAIRE_IAC)
        .assertStringInBody("$.state", QUESTIONNAIRE_STATUS)
        .assertStringInBody("$.questionSet", QUESTIONNAIRE_SET)
        .assertStringInBody("$.dispatchDateTime", QuestionnaireBuilder.QUESTIONNAIRE_DISPATCHDATE_VALUE)
        .assertStringInBody("$.responseDateTime", QuestionnaireBuilder.QUESTIONNAIRE_RESPONSEDATE_VALUE)
        .assertStringInBody("$.receiptDateTime", QuestionnaireBuilder.QUESTIONNAIRE_RECEIPTDATE_VALUE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findQuestionnaireByIacNotFound() {
    with("http://localhost:9998/questionnaires/iac/%s", QUESTIONNAIRE_IAC_NOT_FOUND)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message",
                String.format("%s iac %s", ERRORMSG_QUESTIONNAIRENOTFOUND, QUESTIONNAIRE_IAC_NOT_FOUND))
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findQuestionnaireByCaseid() {
    with("http://localhost:9998/questionnaires/case/%d", QUESTIONNAIRE_CASEID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(2)
        .assertIntegerListInBody("$..questionnaireId", QUESTIONNAIRE_ID_1, QUESTIONNAIRE_ID_2)
        .assertIntegerListInBody("$..caseId", QUESTIONNAIRE_CASEID, QUESTIONNAIRE_CASEID)
        .assertStringListInBody("$..iac", QUESTIONNAIRE_IAC, QUESTIONNAIRE_IAC)
        .assertStringListInBody("$..state", QUESTIONNAIRE_STATUS, QUESTIONNAIRE_STATUS)
        .assertStringListInBody("$..questionSet", QUESTIONNAIRE_SET, QUESTIONNAIRE_SET)
        .assertStringListInBody("$..dispatchDateTime", QuestionnaireBuilder.QUESTIONNAIRE_DISPATCHDATE_VALUE,
            QuestionnaireBuilder.QUESTIONNAIRE_DISPATCHDATE_VALUE)
        .assertStringListInBody("$..responseDateTime", QuestionnaireBuilder.QUESTIONNAIRE_RESPONSEDATE_VALUE,
            QuestionnaireBuilder.QUESTIONNAIRE_RESPONSEDATE_VALUE)
        .assertStringListInBody("$..receiptDateTime", QuestionnaireBuilder.QUESTIONNAIRE_RECEIPTDATE_VALUE,
            QuestionnaireBuilder.QUESTIONNAIRE_RECEIPTDATE_VALUE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findQuestionnaireByCaseidNotFound() {
    with("http://localhost:9998/questionnaires/case/%s", QUESTIONNAIRE_CASEID_NOT_FOUND)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message",
                    String.format("%s case id %s", ERRORMSG_QUESTIONNAIRENOTFOUND, QUESTIONNAIRE_CASEID_NOT_FOUND))
            .andClose();
  }

  /**
   * a test
   */
  @Test
  public void responseOperation() {
    with("http://localhost:9998/questionnaires/%d/response",
        QUESTIONNAIRE_ID_1).put(MediaType.APPLICATION_JSON_TYPE, "")
        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void responseOperationQuestionnaireNotFound() {
    with("http://localhost:9998/questionnaires/%d/response",
            QUESTIONNAIRE_ID_NOT_FOUND).put(MediaType.APPLICATION_JSON_TYPE, "")
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message",
                    String.format("%s questionnaire id %s", ERRORMSG_QUESTIONNAIRENOTFOUND, QUESTIONNAIRE_ID_NOT_FOUND))
        .andClose();
  }

}
