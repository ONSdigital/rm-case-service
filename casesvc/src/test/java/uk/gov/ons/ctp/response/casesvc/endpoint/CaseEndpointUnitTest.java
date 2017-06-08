package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.TestHelper.createTestDate;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.ERRORMSG_CASENOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseGroupEndpoint.ERRORMSG_CASEGROUPNOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.*;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/**
 * Case Endpoint Unit tests
 */
public final class CaseEndpointUnitTest {
  private static final UUID EXISTING_CASE_GROUP_UUID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfeabc");
  private static final UUID EXISTING_CASE_ID_NO_EVENTS = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3999");
  private static final UUID EXISTING_PARTY_UUID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe111");
  private static final UUID CASE_ID_UNCHECKED_EXCEPTION_CASE = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3999");
  private static final UUID CASE1_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd1");
  private static final UUID CASE2_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd2");
  private static final UUID CASE3_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd3");
  private static final UUID CASE4_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd4");
  private static final UUID CASE5_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd5");
  private static final UUID CASE6_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd6");
  private static final UUID CASE7_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd7");
  private static final UUID CASE8_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd8");
  private static final UUID CASE9_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd9");
  private static final UUID CASE9_PARTYID = UUID.fromString("3b136c4b-7a14-4904-9e01-13364dd7b971");
  private static final UUID CASE1_CASEGROUP_ID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfef3c");
  private static final UUID CASE1_CASEGROUP_COLLECTION_EXERCISE_ID = UUID.fromString("dab9db7f-3aa0-4866-be20-54d72ee185fb");
  private static final UUID CASE1_CASEGROUP_PARTY_ID = UUID.fromString("3b136c4b-7a14-4904-9e01-13364dd7b972");
  private static final UUID NON_EXISTING_CASE_GROUP_UUID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe667");
  private static final UUID NON_EXISTING_PARTY_UUID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe666");

  private static final String CASE1_CASEGROUP_SAMPLE_UNIT_REF = "0123456789";
  private static final String CASE1_CASEGROUP_SAMPLE_UNIT_TYPE = "B";
  private static final String CASE_CI_ID = "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e";
  private static final String CASE_PARTY_ID = "3b136c4b-7a14-4904-9e01-13364dd7b972";
  private static final String CASE_ACTIONPLAN_ID_1 = "5381731e-e386-41a1-8462-26373744db81";
  private static final String CASE_SAMPLE_UNIT_TYPE_B = "B";
  private static final String CASE1_DESCRIPTION = "desc 1";
  private static final String CASE2_DESCRIPTION = "desc 2";
  private static final String CASE3_DESCRIPTION = "desc 3";
  private static final String CASE9_DESCRIPTION = "sometest";
  private static final String CASE1_CREATEDBY = "me 1";
  private static final String CASE2_CREATEDBY = "me 2";
  private static final String CASE3_CREATEDBY = "me 3";
  private static final String CASE9_CREATEDBY = "unittest";
  private static final String CASE1_CATEGORY = "ONLINE_QUESTIONNAIRE_RESPONSE";
  private static final String CASE2_CATEGORY = "CLASSIFICATION_INCORRECT";
  private static final String CASE3_CATEGORY = "REFUSAL";
  private static final String CASE9_CATEGORY = "RESPONDENT_ENROLLED";
  private static final String CASE1_SUBCATEGORY = "subcat 1";
  private static final String CASE2_SUBCATEGORY = "subcat 2";
  private static final String CASE3_SUBCATEGORY = "subcat 3";
  private static final String CASE_DATE_VALUE_1 = createTestDate("2016-04-15T17:02:39.699+0100");
  private static final String CASE_DATE_VALUE_2 = createTestDate("2016-04-15T17:02:39.799+0100");
  private static final String CASE_DATE_VALUE_3 = createTestDate("2016-04-15T17:02:39.899+0100");
  private static final String IAC_CASE1 = "bbbb cccc dddd";
  private static final String IAC_CASE2 = "cccc dddd ffff";
  private static final String IAC_CASE3 = "dddd ffff gggg";
  private static final String IAC_CASE4 = "ffff gggg hhhh";
  private static final String IAC_CASE5 = "gggg hhhh jjjj";
  private static final String IAC_CASE6 = "hhhh jjjj kkkk";
  private static final String IAC_CASE7 = "jjjj kkkk llll";
  private static final String IAC_CASE8 = "kkkk llll mmmm";
  private static final String IAC_CASE9 = "kkkk llll mmmm";
  private static final String NON_EXISTING_CASE_ID = "9bc9d99b-9999-99b9-ba99-99f9d9cf9999";
  private static final String NON_EXISTING_IAC = "zzzz llll mmmm";
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  private static final String CASEEVENT_INVALIDJSON =
          "{\"description\":\"a\",\"category\":\"BAD_CAT\",\"createdBy\":\"u\"}";
  private static final String CASEEVENT_VALIDJSON =
          "{\"description\":\"sometest\",\"category\":\"RESPONDENT_ENROLLED\",\"partyId\":\"3b136c4b-7a14-4904-9e01-13364dd7b971\",\"createdBy\":\"unittest\"}";
  private static final String CASEEVENT_VALIDJSON_NO_PARTY =
          "{\"description\":\"sometest\",\"category\":\"RESPONDENT_ENROLLED\",\"createdBy\":\"unittest\"}";

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
  private List<CaseGroup> caseGroupResults;
  private List<CaseEvent> caseEventsResults;
  private List<Category> categoryResults;


  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    this.caseResults = FixtureHelper.loadClassFixtures(Case[].class);
    this.caseGroupResults = FixtureHelper.loadClassFixtures(CaseGroup[].class);
    this.caseEventsResults = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    this.categoryResults = FixtureHelper.loadClassFixtures(Category[].class);
  }

  @Test
  public void findCaseByCaseIdFoundWithCaseEventsAndIac() throws Exception {
    when(caseService.findCaseById(CASE1_ID)).thenReturn(caseResults.get(0));
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class))).thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s?caseevents=true&iac=true", CASE1_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseById"));
    actions.andExpect(jsonPath("$.id", is(CASE1_ID.toString())));
    actions.andExpect(jsonPath("$.iac", is(IAC_CASE1)));
    actions.andExpect(jsonPath("$.collectionInstrumentId", is(CASE_CI_ID)));
    actions.andExpect(jsonPath("$.partyId", is(CASE_PARTY_ID)));
    actions.andExpect(jsonPath("$.actionPlanId", is(CASE_ACTIONPLAN_ID_1)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_SAMPLE_UNIT_TYPE_B)));
    actions.andExpect(jsonPath("$.state", is(CaseDTO.CaseState.SAMPLED_INIT.name())));
    actions.andExpect(jsonPath("$.createdBy", is(SYSTEM)));
    actions.andExpect(jsonPath("$.createdDateTime", is(CASE_DATE_VALUE_1)));

    actions.andExpect(jsonPath("$.responses", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$.responses[*].inboundChannel", containsInAnyOrder(InboundChannel.PAPER.name())));
    actions.andExpect(jsonPath("$.responses[*].dateTime", containsInAnyOrder(CASE_DATE_VALUE_1)));

    actions.andExpect(jsonPath("$.caseGroup.id", is(CASE1_CASEGROUP_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.collectionExerciseId", is(CASE1_CASEGROUP_COLLECTION_EXERCISE_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.partyId", is(CASE1_CASEGROUP_PARTY_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitRef", is(CASE1_CASEGROUP_SAMPLE_UNIT_REF)));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitType", is(CASE1_CASEGROUP_SAMPLE_UNIT_TYPE)));

    actions.andExpect(jsonPath("$.caseEvents", Matchers.hasSize(4)));
    actions.andExpect(jsonPath("$.caseEvents[*].description", containsInAnyOrder(CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION, CASE9_DESCRIPTION)));
    actions.andExpect(jsonPath("$.caseEvents[*].category", containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY, CASE9_CATEGORY)));
    actions.andExpect(jsonPath("$.caseEvents[*].subCategory", containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY, null)));
    actions.andExpect(jsonPath("$.caseEvents[*].createdBy", containsInAnyOrder(CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY, CASE9_CREATEDBY)));
    actions.andExpect(jsonPath("$.caseEvents[*].createdDateTime", containsInAnyOrder(CASE_DATE_VALUE_1, CASE_DATE_VALUE_2, CASE_DATE_VALUE_3, CASE_DATE_VALUE_1)));
  }

  @Test
  public void findCaseByCaseIdFoundWithoutCaseEventsAndIac() throws Exception {
    when(caseService.findCaseById(CASE1_ID)).thenReturn(caseResults.get(0));
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class))).thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", CASE1_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseById"));
    actions.andExpect(jsonPath("$.id", is(CASE1_ID.toString())));
    actions.andExpect(jsonPath("$.iac", is(nullValue())));
    actions.andExpect(jsonPath("$.collectionInstrumentId", is(CASE_CI_ID)));
    actions.andExpect(jsonPath("$.partyId", is(CASE_PARTY_ID)));
    actions.andExpect(jsonPath("$.actionPlanId", is(CASE_ACTIONPLAN_ID_1)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_SAMPLE_UNIT_TYPE_B)));
    actions.andExpect(jsonPath("$.state", is(CaseDTO.CaseState.SAMPLED_INIT.name())));
    actions.andExpect(jsonPath("$.createdBy", is(SYSTEM)));
    actions.andExpect(jsonPath("$.createdDateTime", is(CASE_DATE_VALUE_1)));

    actions.andExpect(jsonPath("$.responses", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$.responses[*].inboundChannel", containsInAnyOrder(InboundChannel.PAPER.name())));
    actions.andExpect(jsonPath("$.responses[*].dateTime", containsInAnyOrder(CASE_DATE_VALUE_1)));

    actions.andExpect(jsonPath("$.caseGroup.id", is(CASE1_CASEGROUP_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.collectionExerciseId", is(CASE1_CASEGROUP_COLLECTION_EXERCISE_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.partyId", is(CASE1_CASEGROUP_PARTY_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitRef", is(CASE1_CASEGROUP_SAMPLE_UNIT_REF)));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitType", is(CASE1_CASEGROUP_SAMPLE_UNIT_TYPE)));

    actions.andExpect(jsonPath("$.caseEvents", is(nullValue())));
  }

  @Test
  public void findCaseByCaseIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", NON_EXISTING_CASE_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseById"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_CASE_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCaseByCaseIdUnCheckedException() throws Exception {
    when(caseService.findCaseById(CASE_ID_UNCHECKED_EXCEPTION_CASE)).thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", CASE_ID_UNCHECKED_EXCEPTION_CASE)));

    actions.andExpect(status().is5xxServerError());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseById"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())));
    actions.andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCasesByPartyIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/partyid/%s", NON_EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesByPartyId"));
  }

  @Test
  public void findCasesByPartyIdFoundWithoutCaseEventsAndIac() throws Exception {
    when(caseService.findCasesByPartyId(EXISTING_PARTY_UUID)).thenReturn(caseResults);
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class))).thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/partyid/%s", EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesByPartyId"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(9)));
    actions.andExpect(jsonPath("$[*].id", containsInAnyOrder(CASE1_ID.toString(), CASE2_ID.toString(), CASE3_ID.toString(), CASE4_ID.toString(), CASE5_ID.toString(), CASE6_ID.toString(), CASE7_ID.toString(), CASE8_ID.toString(), CASE9_ID.toString())));
    actions.andExpect(jsonPath("$[*].iac", containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue())));
    actions.andExpect(jsonPath("$[*].caseEvents", containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue())));
    actions.andExpect(jsonPath("$[*].caseGroup.id", containsInAnyOrder(CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString())));
  }

  @Test
  public void findCasesByPartyIdFoundWithCaseEventsAndIac() throws Exception {
    when(caseService.findCasesByPartyId(EXISTING_PARTY_UUID)).thenReturn(caseResults);
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class))).thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/partyid/%s?caseevents=true&iac=true", EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesByPartyId"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(9)));
    actions.andExpect(jsonPath("$[*].id", containsInAnyOrder(CASE1_ID.toString(), CASE2_ID.toString(), CASE3_ID.toString(), CASE4_ID.toString(), CASE5_ID.toString(), CASE6_ID.toString(), CASE7_ID.toString(), CASE8_ID.toString(), CASE9_ID.toString())));
    actions.andExpect(jsonPath("$[*].iac", containsInAnyOrder(IAC_CASE1, IAC_CASE2, IAC_CASE3, IAC_CASE4, IAC_CASE5, IAC_CASE6, IAC_CASE7, IAC_CASE8, IAC_CASE9)));
    actions.andExpect(jsonPath("$[*].caseEvents", Matchers.hasSize(9)));
    actions.andExpect(jsonPath("$[*].caseGroup.id", containsInAnyOrder(CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString(), CASE1_CASEGROUP_ID.toString())));
  }


  @Test
  public void findCaseByIACNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/iac/%s", NON_EXISTING_IAC)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByIac"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s iac %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_IAC))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCaseByIACWithoutEventsAndIAC() throws Exception {
    when(caseService.findCaseByIac(IAC_CASE1)).thenReturn(caseResults.get(0));
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class))).thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);
    Category newCategory = new Category();
    newCategory.setShortDescription("desc");
    when(categoryService.findCategory(CategoryName.IAC_AUTHENTICATED)).thenReturn(newCategory);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/iac/%s", IAC_CASE1)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByIac"));
    actions.andExpect(jsonPath("$.id", is(CASE1_ID.toString())));
    actions.andExpect(jsonPath("$.iac", is(nullValue())));
    actions.andExpect(jsonPath("$.collectionInstrumentId", is(CASE_CI_ID)));
    actions.andExpect(jsonPath("$.partyId", is(CASE_PARTY_ID)));
    actions.andExpect(jsonPath("$.actionPlanId", is(CASE_ACTIONPLAN_ID_1)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_SAMPLE_UNIT_TYPE_B)));
    actions.andExpect(jsonPath("$.state", is(CaseDTO.CaseState.SAMPLED_INIT.name())));
    actions.andExpect(jsonPath("$.createdBy", is(SYSTEM)));
    actions.andExpect(jsonPath("$.createdDateTime", is(CASE_DATE_VALUE_1)));

    actions.andExpect(jsonPath("$.responses", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$.responses[*].inboundChannel", containsInAnyOrder(InboundChannel.PAPER.name())));
    actions.andExpect(jsonPath("$.responses[*].dateTime", containsInAnyOrder(CASE_DATE_VALUE_1)));

    actions.andExpect(jsonPath("$.caseGroup.id", is(CASE1_CASEGROUP_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.collectionExerciseId", is(CASE1_CASEGROUP_COLLECTION_EXERCISE_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.partyId", is(CASE1_CASEGROUP_PARTY_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitRef", is(CASE1_CASEGROUP_SAMPLE_UNIT_REF)));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitType", is(CASE1_CASEGROUP_SAMPLE_UNIT_TYPE)));

    actions.andExpect(jsonPath("$.caseEvents", is(nullValue())));

    verify(caseService).createCaseEvent(any(CaseEvent.class), any(Case.class));
  }

  @Test
  public void findCasesByCaseGroupIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/casegroupid/%s", NON_EXISTING_CASE_GROUP_UUID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s casegroup id %s", ERRORMSG_CASEGROUPNOTFOUND, NON_EXISTING_CASE_GROUP_UUID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCasesByCaseGroup() throws Exception {
    when(caseGroupService.findCaseGroupById(EXISTING_CASE_GROUP_UUID)).thenReturn(caseGroupResults.get(0));
    when(caseService.findCasesByCaseGroupFK(any())).thenReturn(caseResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/casegroupid/%s", EXISTING_CASE_GROUP_UUID)));
    
    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(9)));
    actions.andExpect(jsonPath("$[*].id", containsInAnyOrder(CASE1_ID.toString(), CASE2_ID.toString(), CASE3_ID.toString(), CASE4_ID.toString(), CASE5_ID.toString(), CASE6_ID.toString(), CASE7_ID.toString(), CASE8_ID.toString(), CASE9_ID.toString())));
    actions.andExpect(jsonPath("$[*].iac", containsInAnyOrder(nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue(), nullValue())));
  }

  @Test
  public void findCasesByCaseGroupEmptyList() throws Exception {
    when(caseGroupService.findCaseGroupById(EXISTING_CASE_GROUP_UUID)).thenReturn(caseGroupResults.get(0));
    when(caseService.findCasesByCaseGroupFK(any())).thenReturn(new ArrayList<>());

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/casegroupid/%s", EXISTING_CASE_GROUP_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
  }

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

  @Test
  public void findCaseEventsByCaseIdFoundButNoEvents() throws Exception {
    when(caseService.findCaseById(EXISTING_CASE_ID_NO_EVENTS)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any())).thenReturn(new ArrayList<>());

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", EXISTING_CASE_ID_NO_EVENTS)));

    actions.andExpect(status().isNoContent());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
  }

  @Test
  public void findCaseEventsByCaseIdFound() throws Exception {
    when(caseService.findCaseById(CASE1_ID)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any())).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", CASE1_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(4)));
    actions.andExpect(jsonPath("$[*].description", containsInAnyOrder(CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION, CASE9_DESCRIPTION)));
    actions.andExpect(jsonPath("$[*].category", containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY, CASE9_CATEGORY)));
    actions.andExpect(jsonPath("$[*].subCategory", containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY, null)));
    actions.andExpect(jsonPath("$[*].createdBy", containsInAnyOrder(CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY, CASE9_CREATEDBY)));
    actions.andExpect(jsonPath("$[*].createdDateTime", containsInAnyOrder(CASE_DATE_VALUE_1, CASE_DATE_VALUE_2, CASE_DATE_VALUE_3, CASE_DATE_VALUE_1)));
  }

  /**
   * a test providing bad json
   */
  @Test
  public void createCaseEventBadJson() throws Exception {
    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_INVALIDJSON));

    actions.andExpect(status().isBadRequest());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())));
    actions.andExpect(jsonPath("$.error.message", isA(String.class)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }
  
  /**
   * a test providing a non existing case ID
   */
  @Test
  public void createCaseEventCaseNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", NON_EXISTING_CASE_ID), CASEEVENT_VALIDJSON));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", isA(String.class)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }
  
  /**
   * a test providing a non existing case ID
   */
  @Test
  public void createCaseEventRequiresNewCase() throws Exception {
    when(categoryService.findCategory(CategoryName.RESPONDENT_ENROLLED)).thenReturn(categoryResults.get(3));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class))).thenReturn(caseEventsResults.get(3));
    when(caseService.findCaseById(CASE9_ID)).thenReturn(caseResults.get(8));
    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_VALIDJSON_NO_PARTY));

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
    when(categoryService.findCategory(CategoryName.RESPONDENT_ENROLLED)).thenReturn(categoryResults.get(3));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class))).thenReturn(caseEventsResults.get(3));
    when(caseService.findCaseById(CASE9_ID)).thenReturn(caseResults.get(8));

    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_VALIDJSON));

    actions.andExpect(status().isCreated());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.caseId", is(CASE9_ID.toString())));
    actions.andExpect(jsonPath("$.description", is(CASE9_DESCRIPTION)));
    actions.andExpect(jsonPath("$.createdBy", is(CASE9_CREATEDBY)));
    actions.andExpect(jsonPath("$.createdDateTime", is(CASE_DATE_VALUE_1)));
    actions.andExpect(jsonPath("$.category", is(CASE9_CATEGORY)));
    actions.andExpect(jsonPath("$.partyId", is(CASE9_PARTYID.toString())));
    actions.andExpect(jsonPath("$.subCategory").doesNotExist());
  }
}
