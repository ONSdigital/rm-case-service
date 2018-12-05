package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.MvcHelper.postJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.ERRORMSG_CASENOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.Example;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindingResult;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.InvalidRequestException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.common.matcher.DateMatcher;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.client.InternetAccessCodeSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/** Case Endpoint Unit tests */
public final class CaseEndpointUnitTest {

  private static final UUID EXISTING_CASE_GROUP_UUID =
      UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfeabc");
  private static final UUID EXISTING_CASE_ID_NO_EVENTS =
      UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3999");
  private static final UUID EXISTING_PARTY_UUID =
      UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe111");
  private static final UUID CASE_ID_UNCHECKED_EXCEPTION_CASE =
      UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3999");
  private static final UUID EXISTING_SURVEY_ID =
      UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");
  private static final String CASE_EVENT_PARTY_UUID = "3b136c4b-7a14-4904-9e01-13364dd7b972";
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
  private static final UUID CASE1_CASEGROUP_ID =
      UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfef3c");
  private static final UUID CASE1_CASEGROUP_COLLECTION_EXERCISE_ID =
      UUID.fromString("dab9db7f-3aa0-4866-be20-54d72ee185fb");
  private static final UUID CASE1_CASEGROUP_PARTY_ID =
      UUID.fromString("3b136c4b-7a14-4904-9e01-13364dd7b972");
  private static final UUID NON_EXISTING_CASE_GROUP_UUID =
      UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe667");
  private static final UUID NON_EXISTING_PARTY_UUID =
      UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe666");

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
  private static final String CASE9_CATEGORY = "RESPONDENT_ENROLED";
  private static final String CASE1_SUBCATEGORY = "subcat 1";
  private static final String CASE2_SUBCATEGORY = "subcat 2";
  private static final String CASE3_SUBCATEGORY = "subcat 3";
  private static final String CASE_DATE_VALUE_1 = "2016-04-15T17:02:39.699+01:00";
  private static final String CASE_DATE_VALUE_2 = "2016-04-15T17:02:39.799+01:00";
  private static final String CASE_DATE_VALUE_3 = "2016-04-15T17:02:39.899+01:00";
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
  private static final String ONE = "1";
  private static final String TWO = "2";
  private static final String THREE = "3";
  private static final String FOUR = "4";
  private static final String FIVE = "5";
  private static final String SIX = "6";
  private static final String SEVEN = "7";
  private static final String EIGHT = "8";
  private static final String NINE = "9";
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  private static final String FINDCASEBYID = "findCaseById";

  private static final String CASEEVENT_INVALIDJSON =
      "{\"description\":\"a\",\"category\":\"BAD_CAT\",\"createdBy\":\"u\"}";
  private static final String CASEEVENT_VALIDJSON =
      "{\"description\":\"sometest\",\"category\":\"RESPONDENT_ENROLED\",\"createdBy\":\"unittest\", "
          + "\"metadata\":{\"partyId\":\"3b136c4b-7a14-4904-9e01-13364dd7b972\"}}";
  private static final String CASEEVENT_VALIDJSON_NO_NEW_CASE =
      "{\"description\":\"sometest\",\"category\":\"GENERAL_ENQUIRY\",\"createdBy\":\"unittest\"}";

  @InjectMocks private CaseEndpoint caseEndpoint;

  @Mock private CategoryService categoryService;

  @Mock private CaseService caseService;

  @Mock private CaseGroupService caseGroupService;

  @Mock private InternetAccessCodeSvcClient internetAccessCodeSvcClient;

  @Mock private CaseRepository caseRepository;

  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;
  private List<Case> caseResults;
  private List<CaseGroup> caseGroupResults;
  private List<CaseEvent> caseEventsResults;
  private List<Category> categoryResults;

  /**
   * Set up of tests
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(caseEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    this.caseResults = FixtureHelper.loadClassFixtures(Case[].class);
    this.caseGroupResults = FixtureHelper.loadClassFixtures(CaseGroup[].class);
    this.caseEventsResults = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    this.categoryResults = FixtureHelper.loadClassFixtures(Category[].class);
  }

  /**
   * Tests if case found by id and checks case events and iac.
   *
   * @throws Exception exception thrown
   */
  @Test
  public void findCaseByCaseIdFoundWithCaseEventsAndIac() throws Exception {
    when(caseService.findCaseById(CASE1_ID)).thenReturn(caseResults.get(0));
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class)))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/%s?caseevents=true&iac=true", CASE1_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName(FINDCASEBYID));
    actions.andExpect(jsonPath("$.id", is(CASE1_ID.toString())));
    actions.andExpect(jsonPath("$.iac", is(IAC_CASE1)));
    actions.andExpect(jsonPath("$.caseRef", is(ONE)));
    actions.andExpect(jsonPath("$.collectionInstrumentId", is(CASE_CI_ID)));
    actions.andExpect(jsonPath("$.partyId", is(CASE_PARTY_ID)));
    actions.andExpect(jsonPath("$.actionPlanId", is(CASE_ACTIONPLAN_ID_1)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_SAMPLE_UNIT_TYPE_B)));
    actions.andExpect(jsonPath("$.state", is(CaseState.SAMPLED_INIT.name())));
    actions.andExpect(jsonPath("$.createdBy", is(SYSTEM)));
    actions.andExpect(jsonPath("$.createdDateTime", is(new DateMatcher(CASE_DATE_VALUE_1))));

    actions.andExpect(jsonPath("$.responses", hasSize(1)));
    actions.andExpect(
        jsonPath("$.responses[*].inboundChannel", containsInAnyOrder(InboundChannel.PAPER.name())));

    actions.andExpect(
        jsonPath("$.responses[*].dateTime", contains(new DateMatcher(CASE_DATE_VALUE_1))));

    actions.andExpect(jsonPath("$.caseGroup.id", is(CASE1_CASEGROUP_ID.toString())));
    actions.andExpect(
        jsonPath(
            "$.caseGroup.collectionExerciseId",
            is(CASE1_CASEGROUP_COLLECTION_EXERCISE_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.partyId", is(CASE1_CASEGROUP_PARTY_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitRef", is(CASE1_CASEGROUP_SAMPLE_UNIT_REF)));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitType", is(CASE1_CASEGROUP_SAMPLE_UNIT_TYPE)));
    actions.andExpect(
        jsonPath("$.caseGroup.caseGroupStatus", is(CaseGroupStatus.NOTSTARTED.toString())));

    actions.andExpect(jsonPath("$.caseEvents", hasSize(4)));
    actions.andExpect(
        jsonPath(
            "$.caseEvents[*].description",
            containsInAnyOrder(
                CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION, CASE9_DESCRIPTION)));
    actions.andExpect(
        jsonPath(
            "$.caseEvents[*].category",
            containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY, CASE9_CATEGORY)));
    actions.andExpect(
        jsonPath(
            "$.caseEvents[*].subCategory",
            containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY, null)));
    actions.andExpect(
        jsonPath(
            "$.caseEvents[*].createdBy",
            containsInAnyOrder(
                CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY, CASE9_CREATEDBY)));
    actions.andExpect(
        jsonPath(
            "$.caseEvents[*].createdDateTime",
            contains(
                new DateMatcher(CASE_DATE_VALUE_1),
                new DateMatcher(CASE_DATE_VALUE_2),
                new DateMatcher(CASE_DATE_VALUE_3),
                new DateMatcher(CASE_DATE_VALUE_1))));
  }

  @Test
  public void findCaseByCaseIdFoundWithoutCaseEventsAndIac() throws Exception {
    when(caseService.findCaseById(CASE1_ID)).thenReturn(caseResults.get(0));
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class)))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", CASE1_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName(FINDCASEBYID));
    actions.andExpect(jsonPath("$.id", is(CASE1_ID.toString())));
    actions.andExpect(jsonPath("$.caseRef", is(ONE)));
    actions.andExpect(jsonPath("$.iac", is(nullValue())));
    actions.andExpect(jsonPath("$.collectionInstrumentId", is(CASE_CI_ID)));
    actions.andExpect(jsonPath("$.partyId", is(CASE_PARTY_ID)));
    actions.andExpect(jsonPath("$.actionPlanId", is(CASE_ACTIONPLAN_ID_1)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_SAMPLE_UNIT_TYPE_B)));
    actions.andExpect(jsonPath("$.state", is(CaseState.SAMPLED_INIT.name())));
    actions.andExpect(jsonPath("$.createdBy", is(SYSTEM)));
    actions.andExpect(jsonPath("$.createdDateTime", is(new DateMatcher(CASE_DATE_VALUE_1))));

    actions.andExpect(jsonPath("$.responses", hasSize(1)));
    actions.andExpect(
        jsonPath("$.responses[*].inboundChannel", containsInAnyOrder(InboundChannel.PAPER.name())));
    actions.andExpect(
        jsonPath("$.responses[*].dateTime", contains(new DateMatcher(CASE_DATE_VALUE_1))));

    actions.andExpect(jsonPath("$.caseGroup.id", is(CASE1_CASEGROUP_ID.toString())));
    actions.andExpect(
        jsonPath(
            "$.caseGroup.collectionExerciseId",
            is(CASE1_CASEGROUP_COLLECTION_EXERCISE_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.partyId", is(CASE1_CASEGROUP_PARTY_ID.toString())));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitRef", is(CASE1_CASEGROUP_SAMPLE_UNIT_REF)));
    actions.andExpect(jsonPath("$.caseGroup.sampleUnitType", is(CASE1_CASEGROUP_SAMPLE_UNIT_TYPE)));
    actions.andExpect(
        jsonPath("$.caseGroup.caseGroupStatus", is(CaseGroupStatus.NOTSTARTED.toString())));

    actions.andExpect(jsonPath("$.caseEvents", is(nullValue())));
  }

  @Test
  public void findCaseByCaseIdNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/%s", NON_EXISTING_CASE_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName(FINDCASEBYID));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(
        jsonPath(
            "$.error.message",
            is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_CASE_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCaseByCaseIdUnCheckedException() throws Exception {
    when(caseService.findCaseById(CASE_ID_UNCHECKED_EXCEPTION_CASE))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/%s", CASE_ID_UNCHECKED_EXCEPTION_CASE)));

    actions.andExpect(status().is5xxServerError());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName(FINDCASEBYID));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())));
    actions.andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCasesByPartyIdNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/partyid/%s", NON_EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesByPartyId"));
  }

  @Test
  public void findCasesByPartyIdFoundWithoutCaseEventsAndIac() throws Exception {
    when(caseService.findCasesByPartyId(EXISTING_PARTY_UUID)).thenReturn(caseResults);
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class)))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/partyid/%s", EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesByPartyId"));
    actions.andExpect(
        jsonPath(
            "$[*].id",
            containsInAnyOrder(
                CASE1_ID.toString(),
                CASE2_ID.toString(),
                CASE3_ID.toString(),
                CASE4_ID.toString(),
                CASE5_ID.toString(),
                CASE6_ID.toString(),
                CASE7_ID.toString(),
                CASE8_ID.toString(),
                CASE9_ID.toString())));
    actions.andExpect(
        jsonPath(
            "$[*].caseRef",
            containsInAnyOrder(ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE)));
    actions.andExpect(
        jsonPath(
            "$[*].iac",
            containsInAnyOrder(
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue())));
    actions.andExpect(
        jsonPath(
            "$[*].caseEvents",
            containsInAnyOrder(
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue(),
                nullValue())));
    actions.andExpect(
        jsonPath(
            "$[*].caseGroup.id",
            containsInAnyOrder(
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString())));
  }

  @Test
  public void findCasesByPartyIdFoundWithCaseEventsAndIac() throws Exception {
    when(caseService.findCasesByPartyId(EXISTING_PARTY_UUID)).thenReturn(caseResults);
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class)))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any(Integer.class))).thenReturn(caseEventsResults);

    ResultActions actions =
        mockMvc.perform(
            getJson(
                String.format("/cases/partyid/%s?caseevents=true&iac=true", EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesByPartyId"));
    actions.andExpect(
        jsonPath(
            "$[*].id",
            containsInAnyOrder(
                CASE1_ID.toString(),
                CASE2_ID.toString(),
                CASE3_ID.toString(),
                CASE4_ID.toString(),
                CASE5_ID.toString(),
                CASE6_ID.toString(),
                CASE7_ID.toString(),
                CASE8_ID.toString(),
                CASE9_ID.toString())));
    actions.andExpect(
        jsonPath(
            "$[*].caseRef",
            containsInAnyOrder(ONE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE)));
    actions.andExpect(
        jsonPath(
            "$[*].iac",
            containsInAnyOrder(
                IAC_CASE1, IAC_CASE2, IAC_CASE3, IAC_CASE4, IAC_CASE5, IAC_CASE6, IAC_CASE7,
                IAC_CASE8, IAC_CASE9)));
    actions.andExpect(jsonPath("$[*].caseEvents", hasSize(9)));
    actions.andExpect(
        jsonPath(
            "$[*].caseGroup.id",
            containsInAnyOrder(
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString(),
                CASE1_CASEGROUP_ID.toString())));
  }

  @Test
  public void findCaseBySurveyId() throws Exception {
    when(caseGroupService.findCaseGroupBySurveyId(EXISTING_SURVEY_ID)).thenReturn(caseGroupResults);
    when(caseGroupService.findCaseGroupByCaseGroupPK(any(Integer.class)))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCasesByCaseGroupFK(any(Integer.class))).thenReturn(caseResults);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/surveyid/%s", EXISTING_SURVEY_ID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesBySurveyId"));

    actions.andExpect(
        jsonPath(
            "$[*].id",
            containsInAnyOrder(
                CASE1_ID.toString(),
                CASE2_ID.toString(),
                CASE3_ID.toString(),
                CASE4_ID.toString(),
                CASE5_ID.toString(),
                CASE6_ID.toString(),
                CASE7_ID.toString(),
                CASE8_ID.toString(),
                CASE9_ID.toString())));
  }

  @Test
  public void findCaseByIACNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/iac/%s", NON_EXISTING_IAC)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByIac"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(
        jsonPath(
            "$.error.message",
            is(String.format("%s iac %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_IAC))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCaseByIAC() throws Exception {
    when(caseService.findCaseByIac(IAC_CASE1)).thenReturn(caseResults.get(0));
    when(categoryService.findCategory(CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT))
        .thenReturn(categoryResults.get(4));

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/iac/%s", IAC_CASE1)));

    actions.andExpect(status().is2xxSuccessful());
  }

  @Test
  public void findCaseByIACButCategoryNotDefined() throws Exception {
    when(caseService.findCaseByIac(IAC_CASE1)).thenReturn(caseResults.get(0));
    when(categoryService.findCategory(CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT))
        .thenReturn(null);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/iac/%s", IAC_CASE1)));

    actions.andExpect(status().is5xxServerError());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseByIac"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())));
    actions.andExpect(
        jsonPath("$.error.message", is(CATEGORY_ACCESS_CODE_AUTHENTICATION_ATTEMPT_NOT_FOUND)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCasesByCaseGroupIdNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(
            getJson(String.format("/cases/casegroupid/%s", NON_EXISTING_CASE_GROUP_UUID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(
        jsonPath(
            "$.error.message",
            is(
                String.format(
                    "CaseGroup not found for casegroup id %s", NON_EXISTING_CASE_GROUP_UUID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCasesByCaseGroup() throws Exception {
    when(caseGroupService.findCaseGroupById(EXISTING_CASE_GROUP_UUID))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCasesByCaseGroupFK(any())).thenReturn(caseResults);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/casegroupid/%s", EXISTING_CASE_GROUP_UUID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
    actions.andExpect(jsonPath("$", hasSize(9)));
    actions.andExpect(
        jsonPath(
            "$[*].id",
            containsInAnyOrder(
                CASE1_ID.toString(),
                CASE2_ID.toString(),
                CASE3_ID.toString(),
                CASE4_ID.toString(),
                CASE5_ID.toString(),
                CASE6_ID.toString(),
                CASE7_ID.toString(),
                CASE8_ID.toString(),
                CASE9_ID.toString())));
  }

  @Test
  public void findCasesByCaseGroupEmptyList() throws Exception {
    when(caseGroupService.findCaseGroupById(EXISTING_CASE_GROUP_UUID))
        .thenReturn(caseGroupResults.get(0));
    when(caseService.findCasesByCaseGroupFK(any())).thenReturn(new ArrayList<>());

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/casegroupid/%s", EXISTING_CASE_GROUP_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
  }

  @Test
  public void findCaseEventsByCaseIdNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/%s/events", NON_EXISTING_CASE_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(
        jsonPath(
            "$.error.message",
            is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, NON_EXISTING_CASE_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCaseEventsByCaseIdFoundButNoEvents() throws Exception {
    when(caseService.findCaseById(EXISTING_CASE_ID_NO_EVENTS)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseFK(any())).thenReturn(new ArrayList<>());

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/cases/%s/events", EXISTING_CASE_ID_NO_EVENTS)));

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
    actions.andExpect(jsonPath("$", hasSize(4)));
    actions.andExpect(jsonPath("$[0].*", hasSize(6)));
    actions.andExpect(
        jsonPath(
            "$[*].description",
            containsInAnyOrder(
                CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION, CASE9_DESCRIPTION)));
    actions.andExpect(
        jsonPath(
            "$[*].category",
            containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY, CASE9_CATEGORY)));
    actions.andExpect(
        jsonPath(
            "$[*].subCategory",
            containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY, null)));
    actions.andExpect(
        jsonPath(
            "$[*].createdBy",
            containsInAnyOrder(
                CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY, CASE9_CREATEDBY)));
    actions.andExpect(
        jsonPath(
            "$[*].createdDateTime",
            contains(
                new DateMatcher(CASE_DATE_VALUE_1),
                new DateMatcher(CASE_DATE_VALUE_2),
                new DateMatcher(CASE_DATE_VALUE_3),
                new DateMatcher(CASE_DATE_VALUE_1))));
    actions.andExpect(jsonPath("$[0].metadata", isA(HashMap.class)));
    actions.andExpect(jsonPath("$[0].metadata.partyId").value(CASE_EVENT_PARTY_UUID));
  }

  @Test
  public void findCaseEventsByCaseIdAndCategoryFoundButNoEvents() throws Exception {
    when(caseService.findCaseById(EXISTING_CASE_ID_NO_EVENTS)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseFKAndCategory(any(), any())).thenReturn(new ArrayList<>());

    ResultActions actions =
        mockMvc.perform(
            getJson(
                String.format(
                    "/cases/%s/events?category=SUCCESSFUL_RESPONSE_UPLOAD",
                    EXISTING_CASE_ID_NO_EVENTS)));

    actions.andExpect(status().isNoContent());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
  }

  @Test
  public void findCaseEventsByCaseIdAndCategoryFound() throws Exception {
    when(caseService.findCaseById(CASE1_ID)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseFKAndCategory(any(), any())).thenReturn(caseEventsResults);

    ResultActions actions =
        mockMvc.perform(
            getJson(
                String.format("/cases/%s/events?category=SUCCESSFUL_RESPONSE_UPLOAD", CASE1_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
    actions.andExpect(jsonPath("$", hasSize(4)));
    actions.andExpect(jsonPath("$[0].*", hasSize(6)));
    actions.andExpect(
        jsonPath(
            "$[*].description",
            containsInAnyOrder(
                CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION, CASE9_DESCRIPTION)));
    actions.andExpect(
        jsonPath(
            "$[*].category",
            containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY, CASE9_CATEGORY)));
    actions.andExpect(
        jsonPath(
            "$[*].subCategory",
            containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY, null)));
    actions.andExpect(
        jsonPath(
            "$[*].createdBy",
            containsInAnyOrder(
                CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY, CASE9_CREATEDBY)));
    actions.andExpect(
        jsonPath(
            "$[*].createdDateTime",
            contains(
                new DateMatcher(CASE_DATE_VALUE_1),
                new DateMatcher(CASE_DATE_VALUE_2),
                new DateMatcher(CASE_DATE_VALUE_3),
                new DateMatcher(CASE_DATE_VALUE_1))));
    actions.andExpect(jsonPath("$[0].metadata", isA(HashMap.class)));
    actions.andExpect(jsonPath("$[0].metadata.partyId").value(CASE_EVENT_PARTY_UUID));
  }

  /**
   * a test providing bad json
   *
   * @throws Exception if the postJson fails
   */
  @Test
  public void createCaseEventBadJson() throws Exception {
    ResultActions actions =
        mockMvc.perform(
            postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_INVALIDJSON));
    actions.andExpect(status().isBadRequest());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())));
    actions.andExpect(jsonPath("$.error.message", isA(String.class)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test providing a non existing case ID
   *
   * @throws Exception exception thrown
   */
  @Test
  public void createCaseEventCaseNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(
            postJson(String.format("/cases/%s/events", NON_EXISTING_CASE_ID), CASEEVENT_VALIDJSON));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", isA(String.class)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test providing good json
   *
   * @throws Exception exception thrown
   */
  @Test
  public void createCaseEventGoodJson() throws Exception {
    when(categoryService.findCategory(CategoryName.RESPONDENT_ENROLED))
        .thenReturn(categoryResults.get(3));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class)))
        .thenReturn(caseEventsResults.get(3));
    when(caseService.findCaseById(CASE9_ID)).thenReturn(caseResults.get(8));

    ResultActions actions =
        mockMvc.perform(postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_VALIDJSON));

    actions.andExpect(status().isCreated());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.*", hasSize(8)));
    actions.andExpect(jsonPath("$.caseId", is(CASE9_ID.toString())));
    actions.andExpect(jsonPath("$.description", is(CASE9_DESCRIPTION)));
    actions.andExpect(jsonPath("$.createdBy", is(CASE9_CREATEDBY)));
    actions.andExpect(jsonPath("$.createdDateTime", is(new DateMatcher(CASE_DATE_VALUE_1))));
    actions.andExpect(jsonPath("$.category", is(CASE9_CATEGORY)));
    actions.andExpect(jsonPath("$.subCategory").doesNotExist());
  }

  /**
   * Checks case event and checks that it's created with metadata and partyId
   *
   * @throws Exception exception thrown
   */
  @Test
  public void createCaseEventWithMetadata() throws Exception {
    // Given
    when(categoryService.findCategory(CategoryName.RESPONDENT_ENROLED))
        .thenReturn(categoryResults.get(3));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class)))
        .thenReturn(caseEventsResults.get(3));
    when(caseService.findCaseById(CASE9_ID)).thenReturn(caseResults.get(8));
    MockHttpServletRequest request = new MockHttpServletRequest();
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));

    // When
    BindingResult bindingResult = mock(BindingResult.class);
    CaseEventCreationRequestDTO caseEventDTO = new CaseEventCreationRequestDTO();
    Map<String, String> metadata = new HashMap<>();
    metadata.put("partyId", CASE_EVENT_PARTY_UUID);
    caseEventDTO.setMetadata(metadata);
    caseEventDTO.setCreatedBy("Test");
    caseEndpoint.createCaseEvent(CASE9_ID, caseEventDTO, bindingResult);

    // Then
    ArgumentCaptor<CaseEvent> caseEventArgumentCaptor = ArgumentCaptor.forClass(CaseEvent.class);
    verify(caseService).createCaseEvent(caseEventArgumentCaptor.capture(), any(Case.class));
    assertEquals(
        CASE_EVENT_PARTY_UUID, caseEventArgumentCaptor.getValue().getMetadata().get("partyId"));
  }

  /**
   * a test creating a new case event with no new case
   *
   * @throws Exception exception thrown
   */
  @Test
  public void createCaseEventNoNewCase() throws Exception {
    when(categoryService.findCategory(CategoryName.GENERAL_ENQUIRY))
        .thenReturn(categoryResults.get(0));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class)))
        .thenReturn(caseEventsResults.get(3));
    when(caseService.findCaseById(CASE9_ID)).thenReturn(caseResults.get(8));

    ResultActions actions =
        mockMvc.perform(
            postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_VALIDJSON_NO_NEW_CASE));

    actions.andExpect(status().isCreated());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("createCaseEvent"));
    actions.andExpect(jsonPath("$.*", hasSize(8)));
    actions.andExpect(jsonPath("$.caseId", is(CASE9_ID.toString())));
    actions.andExpect(jsonPath("$.description", is(CASE9_DESCRIPTION)));
    actions.andExpect(jsonPath("$.createdBy", is(CASE9_CREATEDBY)));
    actions.andExpect(jsonPath("$.createdDateTime", is(new DateMatcher(CASE_DATE_VALUE_1))));
    actions.andExpect(jsonPath("$.category", is(CASE9_CATEGORY)));
    actions.andExpect(jsonPath("$.subCategory").doesNotExist());
  }

  @Test
  public void getCasesByPartyId() throws Exception {
    // Given
    UUID partyId = UUID.randomUUID();
    ArgumentCaptor<Example> captor = ArgumentCaptor.forClass(Example.class);
    when(caseRepository.findAll(captor.capture()))
        .thenReturn(Collections.singletonList(Case.builder().partyId(partyId).build()));

    // When
    ResultActions actions = mockMvc.perform(getJson("/cases").param("partyId", partyId.toString()));

    // Then
    actions.andExpect(status().isOk()).andExpect(jsonPath("$[0].partyId", is(partyId.toString())));
    Example<Case> captured = captor.getValue();
    assertEquals(captured.getProbe().getPartyId(), partyId);
  }

  @Test
  public void getCasesBySampleUnitId() throws Exception {
    // Given
    UUID sampleUnitId = UUID.randomUUID();
    ArgumentCaptor<Example> captor = ArgumentCaptor.forClass(Example.class);
    when(caseRepository.findAll(captor.capture()))
        .thenReturn(Collections.singletonList(Case.builder().sampleUnitId(sampleUnitId).build()));

    // When
    ResultActions actions =
        mockMvc.perform(getJson("/cases").param("sampleUnitId", sampleUnitId.toString()));

    // Then
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sampleUnitId", is(sampleUnitId.toString())));
    Example<Case> captured = captor.getValue();
    assertEquals(captured.getProbe().getSampleUnitId(), sampleUnitId);
  }

  @Test
  public void getCasesNoParams() throws Exception {
    // Given
    UUID sampleUnitId = UUID.randomUUID();
    ArgumentCaptor<Example> captor = ArgumentCaptor.forClass(Example.class);
    when(caseRepository.findAll())
        .thenReturn(Collections.singletonList(Case.builder().sampleUnitId(sampleUnitId).build()));

    // When
    ResultActions actions = mockMvc.perform(getJson("/cases"));

    // Then
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].sampleUnitId", is(sampleUnitId.toString())));
  }

  @Test
  public void getCasesAllParams() throws Exception {
    // Given
    UUID sampleUnitId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    ArgumentCaptor<Example> captor = ArgumentCaptor.forClass(Example.class);
    when(caseRepository.findAll())
        .thenReturn(
            Collections.singletonList(
                Case.builder().sampleUnitId(sampleUnitId).partyId(partyId).build()));

    // When
    ResultActions actions = mockMvc.perform(getJson("/cases"));

    // Then
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].partyId", is(partyId.toString())))
        .andExpect(jsonPath("$[0].sampleUnitId", is(sampleUnitId.toString())));
  }

  @Test
  public void getCasesAndCaseGroups() throws Exception {
    // Given
    int caseGroupFK = 1;
    UUID partyId = UUID.randomUUID();
    when(caseRepository.findAll())
        .thenReturn(Collections.singletonList(Case.builder().caseGroupFK(caseGroupFK).build()));
    when(caseGroupService.findCaseGroupByCaseGroupPK(caseGroupFK))
        .thenReturn(CaseGroup.builder().partyId(partyId).build());

    // When
    ResultActions actions = mockMvc.perform(getJson("/cases"));

    // Then
    actions
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].caseGroup.partyId", is(partyId.toString())));
  }

  /**
   * a test providing good json but versus an incorrect sample unit type
   *
   * @throws Exception exception thrown
   */
  @Test
  public void createCaseEventGoodJsonButVersusIncorrectSampleUnitType() throws Exception {
    when(categoryService.findCategory(CategoryName.RESPONDENT_ENROLED))
        .thenReturn(categoryResults.get(3));
    when(caseService.findCaseById(CASE9_ID)).thenReturn(caseResults.get(8));
    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class)))
        .thenThrow(new CTPException(CTPException.Fault.VALIDATION_FAILED, OUR_EXCEPTION_MESSAGE));

    ResultActions actions =
        mockMvc.perform(postJson(String.format("/cases/%s/events", CASE9_ID), CASEEVENT_VALIDJSON));

    actions
        .andExpect(status().isBadRequest())
        .andExpect(handler().handlerType(CaseEndpoint.class))
        .andExpect(handler().methodName("createCaseEvent"))
        .andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())))
        .andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)))
        .andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test against binding errors
   *
   * @throws Exception exception thrown
   */
  @Test(expected = InvalidRequestException.class)
  public void verifyBadBindingResultThrowsException() throws Exception {
    BindingResult bindingResult = mock(BindingResult.class);
    when(bindingResult.hasErrors()).thenReturn(true);
    CaseEventCreationRequestDTO caseEventDTO = new CaseEventCreationRequestDTO();
    caseEndpoint.createCaseEvent(CASE1_ID, caseEventDTO, bindingResult);
  }
}
