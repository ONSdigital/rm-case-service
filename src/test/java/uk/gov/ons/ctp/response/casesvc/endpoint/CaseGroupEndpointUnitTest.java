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

import java.util.List;
import java.util.UUID;
import ma.glasnost.orika.MapperFacade;
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
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.state.CaseSvcStateTransitionManagerFactory;

/** A test of the CaseGroup endpoint */
public final class CaseGroupEndpointUnitTest {

  private static final UUID CASE_GROUP_UUID = UUID.randomUUID();
  private static final String NON_EXISTENT_CASE_GROUP_UUID = "9a5f2be5-f944-41f9-982c-3517cfcfe666";
  private static final UUID CASE_GROUP_UUID_UNCHECKED_EXCEPTION = UUID.randomUUID();
  private static final UUID CASE_GROUP_CE_ID =
      UUID.fromString("dab9db7f-3aa0-4866-be20-54d72ee185fb");
  private static final UUID CASE_GROUP_PARTY_ID =
      UUID.fromString("3b136c4b-7a14-4904-9e01-13364dd7b972");
  private static final String CASE_GROUP_SU_REF = "0123456789";
  private static final String CASE_GROUP_SU_TYPE = "B";
  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  private static final UUID NON_EXISTING_PARTY_UUID =
      UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfe666");
  private static final UUID EXISTING_PARTY_UUID =
      UUID.fromString("3b136c4b-7a14-4904-9e01-13364dd7b972");
  private static final UUID CASEGROUP1_ID = UUID.fromString("9a5f2be5-f944-41f9-982c-3517cfcfef3c");
  private static final UUID CASEGROUP2_ID = UUID.fromString("2d31f300-246d-11e8-b467-0ed5f89f718b");
  private static final UUID COLLEX1_ID = UUID.fromString("dab9db7f-3aa0-4866-be20-54d72ee185fb");
  private static final UUID COLLEX2_ID = UUID.fromString("24535ac6-246d-11e8-b467-0ed5f89f718b");
  @InjectMocks private CaseGroupEndpoint caseGroupEndpoint;
  @Mock private CaseGroupService caseGroupService;
  @Mock private CaseService caseService;
  @Mock private CategoryService categoryService;
  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  @Spy
  @SuppressWarnings("unchecked")
  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> stm =
      (StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>)
          new CaseSvcStateTransitionManagerFactory()
              .getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_GROUP);

  private MockMvc mockMvc;
  private List<CaseGroup> caseGroupResults;

  /**
   * Initialises Mockito
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(caseGroupEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    this.caseGroupResults = FixtureHelper.loadClassFixtures(CaseGroup[].class);
  }

  /**
   * Tests whether casegroup is found by id
   *
   * @throws Exception exception thrown
   */
  @Test
  public void findCaseGroupById() throws Exception {
    CaseGroup result =
        CaseGroup.builder()
            .id(CASE_GROUP_UUID)
            .collectionExerciseId(CASE_GROUP_CE_ID)
            .partyId(CASE_GROUP_PARTY_ID)
            .sampleUnitRef(CASE_GROUP_SU_REF)
            .sampleUnitType(CASE_GROUP_SU_TYPE)
            .status(CaseGroupStatus.COMPLETE)
            .build();
    when(caseGroupService.findCaseGroupById(CASE_GROUP_UUID)).thenReturn(result);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/casegroups/%s", CASE_GROUP_UUID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.id", is(CASE_GROUP_UUID.toString())));
    actions.andExpect(jsonPath("$.collectionExerciseId", is(CASE_GROUP_CE_ID.toString())));
    actions.andExpect(jsonPath("$.partyId", is(CASE_GROUP_PARTY_ID.toString())));
    actions.andExpect(jsonPath("$.sampleUnitRef", is(CASE_GROUP_SU_REF)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_GROUP_SU_TYPE)));
    actions.andExpect(jsonPath("$.caseGroupStatus", is(CaseGroupStatus.COMPLETE.toString())));
  }

  /**
   * Tests whether casegroup is not found by id
   *
   * @throws Exception exception thrown
   */
  @Test
  public void findCaseGroupByIdNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/casegroups/%s", NON_EXISTENT_CASE_GROUP_UUID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(
        jsonPath(
            "$.error.message",
            is(
                String.format(
                    "CaseGroup not found for casegroup id %s", NON_EXISTENT_CASE_GROUP_UUID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * Tests whether casegroup is found by id with an unchecked exception
   *
   * @throws Exception exception thrown
   */
  @Test
  public void findCaseGroupByIdUnCheckedException() throws Exception {
    when(caseGroupService.findCaseGroupById(CASE_GROUP_UUID_UNCHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions =
        mockMvc.perform(
            getJson(String.format("/casegroups/%s", CASE_GROUP_UUID_UNCHECKED_EXCEPTION)));

    actions.andExpect(status().is5xxServerError());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())));
    actions.andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  @Test
  public void findCaseGroupsByPartyIdNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/casegroups/partyid/%s", NON_EXISTING_PARTY_UUID)));

    actions.andExpect(status().isNoContent());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupsByPartyId"));
  }

  @Test
  public void findCaseGroupsByPartyId() throws Exception {
    when(caseGroupService.findCaseGroupByPartyId(EXISTING_PARTY_UUID)).thenReturn(caseGroupResults);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/casegroups/partyid/%s", EXISTING_PARTY_UUID)));

    actions.andExpect(status().is2xxSuccessful());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupsByPartyId"));
    actions.andExpect(
        jsonPath(
            "$[*].id", containsInAnyOrder(CASEGROUP1_ID.toString(), CASEGROUP2_ID.toString())));
    actions.andExpect(
        jsonPath(
            "$[*].partyId",
            containsInAnyOrder(EXISTING_PARTY_UUID.toString(), EXISTING_PARTY_UUID.toString())));
    actions.andExpect(
        jsonPath(
            "$[*].collectionExerciseId",
            containsInAnyOrder(COLLEX1_ID.toString(), COLLEX2_ID.toString())));
  }
}
