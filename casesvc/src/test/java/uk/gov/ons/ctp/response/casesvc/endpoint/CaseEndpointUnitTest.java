package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseEndpoint.ERRORMSG_CASENOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CaseGroupEndpoint.ERRORMSG_CASEGROUPNOTFOUND;
import static uk.gov.ons.ctp.response.casesvc.utility.Constants.SYSTEM;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import java.util.UUID;

import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
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
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/**
 * Case Endpoint Unit tests
 */
public final class CaseEndpointUnitTest {

  private static final CaseDTO.CaseState CASE1_STATE = CaseDTO.CaseState.SAMPLED_INIT;

  private static final Integer CASE1_ACTIONPLANID = 1;

  private static final Integer EXISTING_CASE_GROUP_PK = 13;
  private static final Integer EXISTING_CASE_PK_NO_EVENTS = 12;
  private static final UUID EXISTING_CASE_ID_NO_EVENTS = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3999");
  private static final String NON_EXISTING_CASE_ID = "9bc9d99b-9999-99b9-ba99-99f9d9cf9999";
 
  private static final UUID CASE_ID_UNCHECKED_EXCEPTION_CASE = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3999");
  private static final UUID CASE1_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd1");
  private static final UUID CASE2_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd2");
  private static final UUID CASE3_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd3");
  private static final UUID CASE4_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd4");
  private static final UUID CASE5_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd5");
  private static final UUID CASE6_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd6");
  private static final UUID CASE7_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd7");
  private static final UUID CASE8_ID = UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3fd8");
  private static final String CASE_CI_ID = "40c7c047-4fb3-4abe-926e-bf19fa2c0a1e";
  private static final String CASE_PARTY_ID = "3b136c4b-7a14-4904-9e01-13364dd7b972";
  private static final String CASE_ACTIONPLAN_ID_1 = "5381731e-e386-41a1-8462-26373744db81";
  private static final String CASE_ACTIONPLAN_ID_2 = "5381731e-e386-41a1-8462-26373744db82";
  private static final String CASE_ACTIONPLAN_ID_3 = "5381731e-e386-41a1-8462-26373744db83";
  private static final String CASE_ACTIONPLAN_ID_4  = "5381731e-e386-41a1-8462-26373744db84";
  private static final String CASE_SAMPLE_UNIT_TYPE_B = "B";
  private static final String CASE_SAMPLE_UNIT_TYPE_BI = "BI";
  private static final String CASE_SAMPLE_UNIT_TYPE_H = "H";
  private static final String CASE_SAMPLE_UNIT_TYPE_HI = "HI";
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
  private static final String CREATEDDATE_VALUE = createTestDate("2016-04-15T17:02:39.699+0100");
//  private static final String CREATEDDATE_VALUE = "2016-04-15T17:02:39.699+0100";

  private static final UUID EXISTING_CASE_GROUP_UUID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfeabc");
  private static final String CASE_GROUP_CE_ID = "dab9db7f-3aa0-4866-be20-54d72ee185fb";
  private static final String CASE_GROUP_PARTY_ID = "3b136c4b-7a14-4904-9e01-13364dd7b972";
  private static final String CASE_GROUP_SU_REF = "0123456789";
  private static final String CASE_GROUP_SU_TYPE = "B";

  private static final UUID NON_EXISTING_CASE_GROUP_UUID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe666");
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  private static final String SAMPLEUNIT_TYPE = "H";

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
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    this.caseResults = FixtureHelper.loadClassFixtures(Case[].class);
    this.caseEventsResults = FixtureHelper.loadClassFixtures(CaseEvent[].class);
    this.categoryResults = FixtureHelper.loadClassFixtures(Category[].class);
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
    CaseGroup result = CaseGroup.builder().id(EXISTING_CASE_GROUP_UUID)
            .caseGroupPK(EXISTING_CASE_GROUP_PK)
            .collectionExerciseId(CASE_GROUP_CE_ID)
            .partyId(CASE_GROUP_PARTY_ID)
            .sampleUnitRef(CASE_GROUP_SU_REF)
            .sampleUnitType(CASE_GROUP_SU_TYPE).build();
    when(caseGroupService.findCaseGroupById(EXISTING_CASE_GROUP_UUID)).thenReturn(result);
    when(caseService.findCasesByCaseGroupFK(EXISTING_CASE_GROUP_PK)).thenReturn(caseResults);

    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/casegroupid/%s", EXISTING_CASE_GROUP_UUID)));

    String case1_ID = CASE1_ID.toString();
    String case2_ID = CASE2_ID.toString();
    String case3_ID = CASE3_ID.toString();
    String case4_ID = CASE4_ID.toString();
    String case5_ID = CASE5_ID.toString();
    String case6_ID = CASE6_ID.toString();
    String case7_ID = CASE7_ID.toString();
    String case8_ID = CASE8_ID.toString();
    
    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCasesInCaseGroup"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(8)));
    actions.andExpect(jsonPath("$[*].id", containsInAnyOrder(case1_ID, case2_ID, case3_ID, case4_ID, case5_ID, case6_ID, case7_ID, case8_ID)));
    actions.andExpect(jsonPath("$[*].collectionInstrumentId", containsInAnyOrder(CASE_CI_ID, CASE_CI_ID, CASE_CI_ID, CASE_CI_ID, CASE_CI_ID, CASE_CI_ID, CASE_CI_ID, CASE_CI_ID)));
    actions.andExpect(jsonPath("$[*].partyId", containsInAnyOrder(CASE_PARTY_ID, CASE_PARTY_ID, CASE_PARTY_ID, CASE_PARTY_ID, CASE_PARTY_ID, CASE_PARTY_ID, CASE_PARTY_ID, CASE_PARTY_ID)));
    actions.andExpect(jsonPath("$[*].actionPlanId", containsInAnyOrder(CASE_ACTIONPLAN_ID_1, CASE_ACTIONPLAN_ID_2, CASE_ACTIONPLAN_ID_3, CASE_ACTIONPLAN_ID_4, CASE_ACTIONPLAN_ID_1, CASE_ACTIONPLAN_ID_2, CASE_ACTIONPLAN_ID_3, CASE_ACTIONPLAN_ID_4)));
    actions.andExpect(jsonPath("$[*].sampleUnitType", containsInAnyOrder(CASE_SAMPLE_UNIT_TYPE_B, CASE_SAMPLE_UNIT_TYPE_BI, CASE_SAMPLE_UNIT_TYPE_H, CASE_SAMPLE_UNIT_TYPE_HI, CASE_SAMPLE_UNIT_TYPE_B, CASE_SAMPLE_UNIT_TYPE_BI, CASE_SAMPLE_UNIT_TYPE_H, CASE_SAMPLE_UNIT_TYPE_HI)));
    actions.andExpect(jsonPath("$[*].state", containsInAnyOrder(CaseDTO.CaseState.SAMPLED_INIT.name(), CaseDTO.CaseState.INACTIONABLE.name(), CaseDTO.CaseState.ACTIONABLE.name(), CaseDTO.CaseState.INACTIONABLE.name(), CaseDTO.CaseState.ACTIONABLE.name(), CaseDTO.CaseState.INACTIONABLE.name(), CaseDTO.CaseState.ACTIONABLE.name(), CaseDTO.CaseState.INACTIONABLE.name())));
    actions.andExpect(jsonPath("$[*].createdBy", containsInAnyOrder(SYSTEM, SYSTEM, SYSTEM, SYSTEM, SYSTEM, SYSTEM, SYSTEM, SYSTEM)));
    actions.andExpect(jsonPath("$[*].createdDateTime", containsInAnyOrder(CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE)));
    actions.andExpect(jsonPath("$[*].responses[*].inboundChannel", containsInAnyOrder(InboundChannel.PAPER.name(), InboundChannel.ONLINE.name(), InboundChannel.PAPER.name(), InboundChannel.ONLINE.name(), InboundChannel.PAPER.name(), InboundChannel.ONLINE.name(), InboundChannel.PAPER.name(), InboundChannel.ONLINE.name())));
    actions.andExpect(jsonPath("$[*].responses[*].dateTime", containsInAnyOrder(CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE, CREATEDDATE_VALUE)));
  }

  
//  TODO:|
//  @Test
//  public void findCaseByCaseIdFound() throws Exception {
//    when(caseService.findCaseById((CASE1_ID)).thenReturn());
//
//    String case1_ID = CASE1_ID.toString();
//    
//    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", case1_ID)));
//
//    actions.andExpect(status().isOk());
//    actions.andExpect(handler().handlerType(CaseEndpoint.class));
//    actions.andExpect(handler().methodName("findCaseById"));
//    actions.andExpect(jsonPath("$.id", is(CASE1_ID)));
//    actions.andExpect(jsonPath("$.collectionInstrumentId", is(CASE_CI_ID)));
//    actions.andExpect(jsonPath("$.partyId", is(CASE_PARTY_ID)));
//    actions.andExpect(jsonPath("$.actionPlanId", is(CASE_ACTIONPLAN_ID_1)));
//    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_SAMPLE_UNIT_TYPE_B)));
//    actions.andExpect(jsonPath("$.state", is(CaseDTO.CaseState.SAMPLED_INIT.name())));
//    actions.andExpect(jsonPath("$.createdBy", is(SYSTEM)));
//    actions.andExpect(jsonPath("$.createdDateTime", is(CREATEDDATE_VALUE)));
//    actions.andExpect(jsonPath("$.responses[*]", Matchers.hasSize(1)));
//  }

  @Test
  public void findCaseByCaseIdNotFound() throws Exception {
   
	String nonExistingCase1Id = NON_EXISTING_CASE_ID.toString();
	  
	ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s", nonExistingCase1Id)));

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


  // TODO
//  /**
//   * a test
//   */
//  @Test
//  public void findCaseEventsByCaseIdFound() throws Exception {
//    when(caseService.findCaseByCasePK(CASE_ID)).thenReturn(caseResults.get(0));
//    when(caseService.findCaseEventsByCaseId(CASE_ID)).thenReturn(caseEventsResults);
//
//    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", CASE_ID)));
//
//    actions.andExpect(status().isOk());
//    actions.andExpect(handler().handlerType(CaseEndpoint.class));
//    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
//    actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
//    actions.andExpect(jsonPath("$[*].casePK", containsInAnyOrder(CASE1_ID, CASE2_ID, CASE3_ID)));
//    actions.andExpect(jsonPath("$[*].description", containsInAnyOrder(CASE1_DESCRIPTION, CASE2_DESCRIPTION, CASE3_DESCRIPTION)));
//    actions.andExpect(jsonPath("$[*].createdBy", containsInAnyOrder(CASE1_CREATEDBY, CASE2_CREATEDBY, CASE3_CREATEDBY)));
////    actions.andExpect(jsonPath("$[*].createdDateTime", containsInAnyOrder(CREATEDDATE_VALUE, CREATEDDATE_VALUE1, CREATEDDATE_VALUE2)));
//    actions.andExpect(jsonPath("$[*].category", containsInAnyOrder(CASE1_CATEGORY, CASE2_CATEGORY, CASE3_CATEGORY)));
//    actions.andExpect(jsonPath("$[*].subCategory", containsInAnyOrder(CASE1_SUBCATEGORY, CASE2_SUBCATEGORY, CASE3_SUBCATEGORY)));
//  }

  /**
   * a test
   */
  @Test
  public void findCaseEventsByCaseFKFoundButNoEvents() throws Exception {
    when(caseService.findCaseById(EXISTING_CASE_ID_NO_EVENTS)).thenReturn(caseResults.get(0));
    when(caseService.findCaseEventsByCaseFK(EXISTING_CASE_PK_NO_EVENTS)).thenReturn(new ArrayList<>());

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
    ResultActions actions = mockMvc.perform(getJson(String.format("/cases/%s/events", EXISTING_CASE_ID_NO_EVENTS)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseEndpoint.class));
    actions.andExpect(handler().methodName("findCaseEventsByCaseId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("%s case id %s", ERRORMSG_CASENOTFOUND, EXISTING_CASE_ID_NO_EVENTS))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }


  // TODO
//  /**
//   * a test providing bad json
//   */
//  @Test
//  public void createCaseEventBadJson() throws Exception {
//    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE_ID), CASEEVENT_INVALIDJSON));
//
//    actions.andExpect(status().isBadRequest());
//    actions.andExpect(handler().handlerType(CaseEndpoint.class));
//    actions.andExpect(handler().methodName("createCaseEvent"));
//    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.VALIDATION_FAILED.name())));
//    actions.andExpect(jsonPath("$.error.message", isA(String.class)));
//    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
//  }

  // TODO
//  /**
//   * a test providing good json
//   */
//  @Test
//  public void createCaseEventGoodJson() throws Exception {
//    when(categoryService.findCategory(CategoryDTO.CategoryType.GENERAL_ENQUIRY)).thenReturn(categoryResults.get(0));
//    when(caseService.createCaseEvent(any(CaseEvent.class), any(Case.class))).thenReturn(caseEventsResults.get(0));
//
//    ResultActions actions = mockMvc.perform(postJson(String.format("/cases/%s/events", CASE_ID), CASEEVENT_VALIDJSON));
//
//    actions.andExpect(status().isCreated());
//    actions.andExpect(handler().handlerType(CaseEndpoint.class));
//    actions.andExpect(handler().methodName("createCaseEvent"));
//    actions.andExpect(jsonPath("$.caseEventPK", is(CASE1_ID)));
//    actions.andExpect(jsonPath("$.casePK", is(CASE1_ID)));
//    actions.andExpect(jsonPath("$.description", is(CASE1_DESCRIPTION)));
//    actions.andExpect(jsonPath("$.createdBy", is(CASE1_CREATEDBY)));
////    actions.andExpect(jsonPath("$.createdDateTime", is(CREATEDDATE_VALUE)));
//    actions.andExpect(jsonPath("$.category", is(CASE1_CATEGORY)));
//    actions.andExpect(jsonPath("$.subCategory", is(CASE1_SUBCATEGORY)));
//  }


  private static void printDate(ZonedDateTime zdt) {
    TimeZone gmt = TimeZone.getTimeZone("GMT");
    TimeZone utc = TimeZone.getTimeZone("UTC");
    TimeZone cet = TimeZone.getTimeZone("CET");
    System.out.println(CREATEDDATE_VALUE);
    System.out.println(zdt);
    LocalDateTime ldt = zdt.toLocalDateTime();
    ZonedDateTime gmtTime = ldt.atZone(ZoneId.of(gmt.getID()));
    System.out.println("gmt:" + gmtTime);
    ZonedDateTime utcTime = ldt.atZone(ZoneId.of(utc.getID()));
    System.out.println("utc:" + utcTime);
    ZonedDateTime cetTime = ldt.atZone(ZoneId.of(cet.getID()));
    System.out.println("cet:" + cetTime);
    ZonedDateTime bstTime = ldt.atZone(ZoneId.of("Europe/London"));
    System.out.println("bst:" + bstTime);
    System.out.println(Calendar.getInstance().getTimeZone().getID());
  }
  
  private static String createTestDate(String date){
    String zoneId = Calendar.getInstance().getTimeZone().getID();
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    ZonedDateTime zdt = ZonedDateTime.parse(date, formatter);
    LocalDateTime ldt = zdt.toLocalDateTime();
    ZonedDateTime compareDate = ldt.atZone(ZoneId.of(zoneId));
    return formatter.format(compareDate);
    //return compareDate.toString();
    
  }
}
