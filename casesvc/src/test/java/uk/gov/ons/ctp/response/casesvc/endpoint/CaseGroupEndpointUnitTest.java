package uk.gov.ons.ctp.response.casesvc.endpoint;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 */
public final class CaseGroupEndpointUnitTest {

  @InjectMocks
  private CaseGroupEndpoint caseGroupEndpoint;

  @Mock
  private CaseGroupService caseGroupService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;

  private static final String CASE_GROUP_UUID = "9a5f2be5-f944-41f9-982c-3517cfcfef3c";
  private static final String NON_EXISTENT_CASE_GROUP_UUID = "9a5f2be5-f944-41f9-982c-3517cfcfe666";
  private static final String CASE_GROUP_CE_ID = "dab9db7f-3aa0-4866-be20-54d72ee185fb";
  private static final String CASE_GROUP_PARTY_ID = "3b136c4b-7a14-4904-9e01-13364dd7b972";
  private static final String CASE_GROUP_SU_REF = "0123456789";
  private static final String CASE_GROUP_SU_TYPE = "B";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseGroupEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
  }

  @Test
  public void findCaseGroupByIdFound() throws Exception {
    CaseGroup result = CaseGroup.builder().id(CASE_GROUP_UUID)
            .collectionExerciseID(CASE_GROUP_CE_ID)
            .partyID(CASE_GROUP_PARTY_ID)
            .sampleUnitRef(CASE_GROUP_SU_REF)
            .sampleUnitType(CASE_GROUP_SU_TYPE).build();
    when(caseGroupService.findCaseGroupById(CASE_GROUP_UUID)).thenReturn(result);

    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/%s", CASE_GROUP_UUID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.id", is(CASE_GROUP_UUID)));
    actions.andExpect(jsonPath("$.collectionExerciseID", is(CASE_GROUP_CE_ID)));
    actions.andExpect(jsonPath("$.partyID", is(CASE_GROUP_PARTY_ID)));
    actions.andExpect(jsonPath("$.sampleUnitRef", is(CASE_GROUP_SU_REF)));
    actions.andExpect(jsonPath("$.sampleUnitType", is(CASE_GROUP_SU_TYPE)));
  }

  @Test
  public void findCaseGroupByIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/%s", NON_EXISTENT_CASE_GROUP_UUID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("CaseGroup not found for casegroup id %s", NON_EXISTENT_CASE_GROUP_UUID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }
}
