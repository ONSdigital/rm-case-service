package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.ERRORMSG_CASENOTFOUND;

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
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

import java.util.ArrayList;
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
  private static final Integer EXISTING_CASE_ID_NO_EVENTS = 992;
  private static final Integer NON_EXISTING_CASE_ID = 998;
  private static final Integer UNCHECKED_EXCEPTION_CASE_ID = 999;

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
  private static final String CREATEDDATE_VALUE = "2016-04-15T16:02:39.699+0000";
  private static final String CREATEDDATE_VALUE1 = "2016-04-15T16:02:39.799+0000";
  private static final String CREATEDDATE_VALUE2 = "2016-04-15T16:02:39.899+0000";
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  private static final String CASEEVENT_INVALIDJSON =
          "{\"description\":\"a\",\"category\":\"BAD_CAT\",\"createdBy\":\"u\"}";
  private static final String CASEEVENT_VALIDJSON =
          "{\"description\":\"sometest\",\"category\":\"GENERAL_ENQUIRY\",\"createdBy\":\"unittest\"}";

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
  private List<Category> categoryResults;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .build();

    this.caseResults = FixtureHelper.loadClassFixtures(Case[].class);
    this.caseEventsResults = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    this.categoryResults = FixtureHelper.loadClassFixtures(Category[].class);
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
    actions.andExpect(jsonPath("$.createdDateTime", is(CREATEDDATE_VALUE)));
    actions.andExpect(jsonPath("$.createdBy", is(CASE1_CREATEDBY)));
    actions.andExpect(jsonPath("$.actionPlanMappingId", is(CASE1_ACTIONPLANMAPPINGID)));
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", NON_EXISTING_CASE_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByCaseId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_CASE_ID))));
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
    actions.andExpect(jsonPath("$[*].createdDateTime", containsInAnyOrder(CREATEDDATE_VALUE, CREATEDDATE_VALUE1, CREATEDDATE_VALUE2)));
    actions.andExpect(jsonPath("$[*].category", containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY)));
    actions.andExpect(jsonPath("$[*].subCategory", containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY)));
  }

  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseIdFoundButNoEvents() throws Exception {
    when(caseService.findCaseByCaseId(EXISTING_CASE_ID_NO_EVENTS)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseId(EXISTING_CASE_ID_NO_EVENTS)).thenReturn(new ArrayList<>());

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", EXISTING_CASE_ID_NO_EVENTS)));

    actions.andExpect(status().isNoContent());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
  }


  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", NON_EXISTING_CASE_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_CASE_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test
   */
  @Test
  public void findCaseByCaseIdUnCheckedException() throws Exception {
    when(caseService.findCaseByCaseId(UNCHECKED_EXCEPTION_CASE_ID)).thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", UNCHECKED_EXCEPTION_CASE_ID)));

    actions.andExpect(status().is5xxServerError());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByCaseId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())));
    actions.andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test providing bad json
   */
  @Test
  public void createCaseEventBadJson() throws Exception {
    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE_ID), CASEEVENT_INVALIDJSON));

    actions.andExpect(status().isBadRequest());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())));
    actions.andExpect(jsonPath("$.error.message", isA(String.class)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test providing good json
   */
  @Test
  public void createCaseEventGoodJson() throws Exception {
    when(categoryService.findCategory(CategoryDTO.CategoryType.GENERAL_ENQUIRY)).thenReturn(categoryResults.get(0));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class))).thenReturn(caseEventsResults.get(0));

    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE_ID), CASEEVENT_VALIDJSON));

    actions.andExpect(status().isCreated());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.caseEventId", is(CASE1_ID)));
    actions.andExpect(jsonPath("$.caseId", is(CASE1_ID)));
    actions.andExpect(jsonPath("$.description", is(CASE1_DESCRIPTION)));
    actions.andExpect(jsonPath("$.createdBy", is(CASE1_CREATEDBY)));
    actions.andExpect(jsonPath("$.createdDateTime", is(CREATEDDATE_VALUE)));
    actions.andExpect(jsonPath("$.category", is(CASE1_CATEGORY)));
    actions.andExpect(jsonPath("$.subCategory", is(CASE1_SUBCATEGORY)));
  }
}
