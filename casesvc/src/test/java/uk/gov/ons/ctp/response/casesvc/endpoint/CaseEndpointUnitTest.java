package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.ERRORMSG_CASENOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import ma.glasnost.orika.MapperFacade;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

import java.util.List;

/**
 * Case Endpoint Unit tests
 */
public final class CaseEndpointUnitTest {

  private static final CaseDTO.CaseState CASE1_STATE = CaseDTO.CaseState.SAMPLED_INIT;

  private static final Integer CASE_ID = 124;
  private static final Integer CASE_TYPEID = 1;
  private static final Integer CASE1_ACTIONPLANMAPPINGID = 1;
  private static final Integer CASE1_ID = 1;
  private static final Integer CASE2_ID = 2;
  private static final Integer CASE3_ID = 3;
  private static final Integer NON_EXISTING_ID = 998;

  private static final Long CASE1_CREATED_DATE = 1460736159699L;
  private static final Long CASE2_CREATED_DATE = 1460736159799L;
  private static final Long CASE3_CREATED_DATE = 1460736159899L;

  private static final String CASE1_DESCRIPTION = "desc 1";
  private static final String CASE2_DESCRIPTION = "desc 2";
  private static final String CASE3_DESCRIPTION = "desc 3";
  private static final String CASE1_CREATEDBY = "me 1";
  private static final String CASE2_CREATEDBY = "me 2";
  private static final String CASE3_CREATEDBY = "me 3";
  private static final String CASE1_CATEGORY = "ONLINE_QUESTIONNAIRE_RESPONSE";
  private static final String CASE2_CATEGORY = "CLASSIFICATION_INCORRECT";
  private static final String CASE3_CATEGORY = "REFUSAL";
  private static final String CASE1_SUBCATEGORY = "subcat 1";
  private static final String CASE2_SUBCATEGORY = "subcat 2";
  private static final String CASE3_SUBCATEGORY = "subcat 3";

  @InjectMocks
  private CaseEndpoint caseEndpoint;

  @Mock
  private CategoryService categoryService;

  @Mock
  private CaseService caseService;

  @Mock
  private CaseGroupService caseGroupService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;
  private List<Case> caseResults;
  private List<CaseEvent> caseEventsResults;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .build();

    this.caseResults = FixtureHelper.loadClassFixtures(Case[].class);
    this.caseEventsResults = FixtureHelper.loadClassFixtures(CaseEvent[].class);
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdFound() throws Exception {
    when(caseService.findCaseByCaseId(CASE_ID)).thenReturn(caseResults.get(0));

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", CASE_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByCaseId"));
    actions.andExpect(jsonPath("$.state", is(CASE1_STATE.name())));
    actions.andExpect(jsonPath("$.caseTypeId", is(CASE_TYPEID)));
    actions.andExpect(jsonPath("$.createdDateTime", is(CASE1_CREATED_DATE)));
    actions.andExpect(jsonPath("$.createdBy", is(CASE1_CREATEDBY)));
    actions.andExpect(jsonPath("$.actionPlanMappingId", is(CASE1_ACTIONPLANMAPPINGID)));
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", NON_EXISTING_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByCaseId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseIdFound() throws Exception {
    when(caseService.findCaseByCaseId(CASE_ID)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseId(CASE_ID)).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", CASE_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
    actions.andExpect(jsonPath("$[*].caseId", containsInAnyOrder(CASE1_ID, CASE2_ID, CASE3_ID)));
    actions.andExpect(jsonPath("$[*].description", containsInAnyOrder(CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION)));
    actions.andExpect(jsonPath("$[*].createdBy", containsInAnyOrder(CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY)));
    actions.andExpect(jsonPath("$[*].createdDateTime", containsInAnyOrder(CASE1_CREATED_DATE, CASE2_CREATED_DATE, CASE3_CREATED_DATE)));
    actions.andExpect(jsonPath("$[*].category", containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY)));
    actions.andExpect(jsonPath("$[*].subCategory", containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY)));
  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseEventsByCaseIdFoundButNoEvents() {
//    with("/cases/%s/events", EXISTING_ID_NO_EVENTS)
//        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseEventsByCaseIdNotFound() {
//    with("/cases/%s/events", NON_EXISTING_ID)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//        .assertTimestampExists()
//        .assertMessageEquals(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_ID))
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseByCaseIdUnCheckedException() {
//    with("/cases/%s", UNCHECKED_EXCEPTION)
//        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
//        .assertFaultIs(CTPException.Fault.SYSTEM_ERROR)
//        .assertTimestampExists()
//        .assertMessageEquals(OUR_EXCEPTION_MESSAGE)
//        .andClose();
//  }
//
//  /**
//   * a test providing bad json
//   */
//  @Test
//  public void createCaseEventBadJson() {
//    with("/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_INVALIDJSON)
//        .assertResponseCodeIs(HttpStatus.BAD_REQUEST)
//        .andClose();
//  }
//
//  /**
//   * a test providing good json
//   */
//  @Test
//  public void createCaseEventGoodJson() {
//    with("/cases/%s/events", CASEID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_VALIDJSON)
//        .assertResponseCodeIs(HttpStatus.CREATED)
//        .assertIntegerInBody("$.caseEventId", 1)
//        .assertIntegerInBody("$.caseId", CASEID)
//        .assertStringInBody("$.description", CASEEVENT_DESC1)
//        .assertStringInBody("$.createdBy", CREATEDBY)
//        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
//        .assertStringInBody("$.category", CASEEVENT_CATEGORY.name())
//        .assertStringInBody("$.subCategory", CASEEVENT_SUBCATEGORY)
//        .andClose();
//  }
//
//  /**
//   * a test providing good json
//   */
//  @Test
//  public void createCaseEventCaseNotFound() {
//    with("/cases/%s/events", NON_EXISTING_ID).post(MediaType.APPLICATION_JSON_TYPE, CASEEVENT_VALIDJSON)
//        .assertResponseCodeIs(HttpStatus.CREATED)
//        .assertIntegerInBody("$.caseEventId", 1)
//        .assertIntegerInBody("$.caseId", CASEID)
//        .assertStringInBody("$.description", CASEEVENT_DESC1)
//        .assertStringInBody("$.createdBy", CREATEDBY)
//        .assertStringInBody("$.createdDateTime", CREATEDDATE_VALUE)
//        .assertStringInBody("$.category", CASEEVENT_CATEGORY.name())
//        .assertStringInBody("$.subCategory", CASEEVENT_SUBCATEGORY)
//        .andClose();
//  }
}
