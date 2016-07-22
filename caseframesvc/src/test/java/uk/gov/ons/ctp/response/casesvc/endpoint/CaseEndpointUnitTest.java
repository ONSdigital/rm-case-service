package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE1_ACTIONPLANID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE1_SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE1_TYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE2_ACTIONPLANID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE2_SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE2_TYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE3_ACTIONPLANID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE3_SAMPLEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE3_TYPEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_CATEGORY;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_DESC1;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_DESC2;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_DESC3;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEEVENT_SUBCATEGORY;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASEID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE_QUESTIONSET;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE_STATE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CASE_SURVEYID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CREATEDBY;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.CREATEDDATE_VALUE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.NON_EXISTING_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.QUESTIONNAIREID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.UNCHECKED_EXCEPTION;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory.UPRN;

import javax.ws.rs.core.Application;
import javax.ws.rs.core.MediaType;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jaxrs.CTPMessageBodyReader;
import uk.gov.ons.ctp.common.jaxrs.GeneralExceptionMapper;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseServiceFactory;

/**
 * Case Endpoint Unit tests
 */
public final class CaseEndpointUnitTest extends CTPJerseyTest {

  private static final String CASEEVENT_INVALIDJSON =
      "{\"description\":\"a\",\"category\":\"\",\"createdBy\":\"u\"}";
  private static final String CASEEVENT_INTEGRATION_SAMPLE_INVALIDJSON =
          "{\"description\":\" O Deep Thought computer,' he said, 'the task we have designed you to perform is this. We want you to tell us....' he paused, 'The Answer.' 'The Answer?' said Deep Thought. 'The Answer to what?' 'Life!' urged Fook.  'The Universe!' said Lunkwill.  'Everything!' they said in chorus.  Deep Thought paused for a moment's reflection.  'Tricky,' he said finally.  'But can you do it?' Again, a significant pause.  'Yes,' said Deep Thought, 'I can do it.' 'There is an answer?' said Fook with breathless excitement.  'Yes,' said Deep Thought. 'Life, the Universe, and Everything. There is an answer. But, I'll have to think about it.' ...  Fook glanced impatiently at his watch.  “How long?” he said.  “Seven and a half million years,” said Deep Thought.  Lunkwill and Fook blinked at each other.  “Seven and a half million years...!” they cried in chorus.  “Yes,” declaimed Deep Thought, “I said I’d have to think about it, didn’t I?' [Seven and a half million years later.... Fook and Lunkwill are long gone, but their ancestors continue what they started] 'We are the ones who will hear,' said Phouchg, 'the answer to the great question of Life....!' 'The Universe...!' said Loonquawl.  'And Everything...!' 'Shhh,' said Loonquawl with a slight gesture. 'I think Deep Thought is preparing to speak!' There was a moment's expectant pause while panels slowly came to life on the front of the console. Lights flashed on and off experimentally and settled down into a businesslike pattern. A soft low hum came from the communication channel.  'Good Morning,' said Deep Thought at last.  'Er..good morning, O Deep Thought' said Loonquawl nervously, 'do you have...er, that is...' 'An Answer for you?' interrupted Deep Thought majestically. 'Yes, I have.' The two men shivered with expectancy. Their waiting had not been in vain.  'There really is one?' breathed Phouchg.  'There really is one,' confirmed Deep Thought.  'To Everything? To the great Question of Life, the Universe and everything?' 'Yes.' Both of the men had been trained for this moment, their lives had been a preparation for it, they had been selected at birth as those who would witness the answer, but even so they found themselves gasping and squirming like excited children.  'And you're ready to give it to us?' urged Loonsuawl.  'I am.' 'Now?' 'Now,' said Deep Thought.  They both licked their dry lips.  'Though I don't think,' added Deep Thought. 'that you're going to like it.' 'Doesn't matter!' said Phouchg. 'We must know it! Now!' 'Now?' inquired Deep Thought.  'Yes! Now...' 'All right,' said the computer, and settled into silence again. The two men fidgeted. The tension was unbearable.  'You're really not going to like it,' observed Deep Thought.  'Tell us!' 'All right,' said Deep Thought. 'The Answer to the Great Question...' 'Yes..!' 'Of Life, the Universe and Everything...' said Deep Thought.  'Yes...!' 'Is...' said Deep Thought, and paused.  'Yes...!' 'Is...' 'Yes...!!!...?' 'Forty-two,' said Deep Thought, with infinite majesty and calm.\",\n" +
                  "   \"category\":\"General Enquiry\",\n" +
                  "   \"createdBy\":\"collect.cso\"\n" +
                  "}";
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
        .assertLongOccursThroughoutListInBody("$..uprn", UPRN)
        .assertStringOccursThroughoutListInBody("$..state", CASE_STATE.name())
        .assertIntegerListInBody("$..caseTypeId", CASE1_TYPEID, CASE2_TYPEID, CASE3_TYPEID)
        .assertStringOccursThroughoutListInBody("$..createdDateTime", CREATEDDATE_VALUE)
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
        .assertLongInBody("$.uprn", UPRN)
        .assertStringInBody("$.state", CASE_STATE.name())
        .assertIntegerInBody("$.caseTypeId", CASE1_TYPEID)
        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
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
        .assertLongInBody("$.uprn", UPRN)
        .assertStringInBody("$.state", CASE_STATE.name())
        .assertIntegerInBody("$.caseTypeId", CASE1_TYPEID)
        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
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
        .assertStringOccursThroughoutListInBody("$..createdDateTime", CREATEDDATE_VALUE)
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
   * a test providing bad json
   */
  @Test
  public void createCaseEventBadJson() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_INVALIDJSON)
        .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
        .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
        .assertTimestampExists()
        .assertMessageEquals(GeneralExceptionMapper.JSON_FAILS_VALIDATION)
        .andClose();
  }

  /**
   * a test providing good json
   */
  @Test
  public void createCaseEventGoodJson() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_VALIDJSON)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.caseEventId", 1)
        .assertIntegerInBody("$.caseId", CASEID)
        .assertStringInBody("$.description", CASEEVENT_DESC1)
        .assertStringInBody("$.createdBy", CREATEDBY)
        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
        .assertStringInBody("$.category", CASEEVENT_CATEGORY)
        .assertStringInBody("$.subCategory", CASEEVENT_SUBCATEGORY)
        .andClose();
  }

  /**
   * a test providing bad json (description has more than 100 characters) replicating scenario from Steve Goddard
   */
  @Test
  public void createCaseEventBadJsonIntegrationTestScenario() {
    with("http://localhost:9998/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_INTEGRATION_SAMPLE_INVALIDJSON)
            .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
            .assertFaultIs(CTPException.Fault.VALIDATION_FAILED)
            .assertTimestampExists()
            .assertMessageEquals(GeneralExceptionMapper.JSON_FAILS_VALIDATION)
            .andClose();
  }
}
