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

  private static final Integer CASE_GROUP_ID = 1;
  private static final Integer SAMPLE_ID = 2;
  private static final Integer NON_EXISTENT_CASE_GROUP_ID = 99;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseGroupEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupByCaseGroupIdFound() throws Exception {
    when(caseGroupService.findCaseGroupByCaseGroupId(CASE_GROUP_ID)).thenReturn(CaseGroup.builder().caseGroupId(CASE_GROUP_ID).sampleId(SAMPLE_ID).build());

    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/%s", CASE_GROUP_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.caseGroupId", is(CASE_GROUP_ID)));
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupByCaseGroupIdNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/%s", NON_EXISTENT_CASE_GROUP_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("CaseGroup not found for casegroup id %s", NON_EXISTENT_CASE_GROUP_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
   }

  
}
