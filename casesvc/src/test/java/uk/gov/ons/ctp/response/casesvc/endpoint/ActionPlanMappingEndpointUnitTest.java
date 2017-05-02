package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;

import ma.glasnost.orika.MapperFacade;
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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.representation.OutboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

import java.util.ArrayList;
import java.util.List;

public final class ActionPlanMappingEndpointUnitTest {

  @InjectMocks
  private ActionPlanMappingEndpoint actionPlanMappingEndpoint;

  @Mock
  private ActionPlanMappingService actionPlanMappingService;

  @Mock
  private CaseTypeService caseTypeService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;

  private static final Integer ONE = 1;
  private static final Integer CASE_TYPE_ID = 1;
  private static final Integer NON_EXISTENT_MAPPING_ID = 9;
  private static final String VARIANT_ENG = "ENGLISH";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(actionPlanMappingEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();
  }

  /**
   * a test
   */
  @Test
  public void findMappingFound() throws Exception {
    when(actionPlanMappingService.findActionPlanMapping(ONE)).thenReturn(buildActionPlanMapping());

    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplanmappings/%s", ONE)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(ActionPlanMappingEndpoint.class));
    actions.andExpect(handler().methodName("findActionPlanMappingByActionPlanMappingId"));
    actions.andExpect(jsonPath("$.actionPlanMappingId", is(ONE)));
    actions.andExpect(jsonPath("$.actionPlanId", is(ONE)));
    actions.andExpect(jsonPath("$.caseTypeId", is(ONE)));
    actions.andExpect(jsonPath("$.isDefault", is(true)));
    actions.andExpect(jsonPath("$.inboundChannel", is(InboundChannel.ONLINE.name())));
    actions.andExpect(jsonPath("$.variant", is(VARIANT_ENG)));
    actions.andExpect(jsonPath("$.outboundChannel", is(OutboundChannel.POST.name())));
  }

  /**
   * a test
   */
  @Test
  public void findMappingNotFound() throws Exception {
    when(actionPlanMappingService.findActionPlanMapping(NON_EXISTENT_MAPPING_ID)).thenReturn(null);

    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplanmappings/%s", NON_EXISTENT_MAPPING_ID)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(ActionPlanMappingEndpoint.class));
    actions.andExpect(handler().methodName("findActionPlanMappingByActionPlanMappingId"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("ActionPlanMapping not found for id %s", NON_EXISTENT_MAPPING_ID))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test
   */
  @Test
  public void findMappingsByCaseTypeId() throws Exception {
    CaseType caseType = new CaseType();
    when(caseTypeService.findCaseTypeByCaseTypeId(CASE_TYPE_ID)).thenReturn(caseType);

    List<ActionPlanMapping> list = new ArrayList<>();
    list.add(buildActionPlanMapping());
    when(actionPlanMappingService.findActionPlanMappingsForCaseType(CASE_TYPE_ID)).thenReturn(list);

    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplanmappings/casetype/%d", CASE_TYPE_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(ActionPlanMappingEndpoint.class));
    actions.andExpect(handler().methodName("findActionPlanMappingByCaseTypeId"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$[0].actionPlanMappingId", is(ONE)));
    actions.andExpect(jsonPath("$[0].actionPlanId", is(ONE)));
    actions.andExpect(jsonPath("$[0].caseTypeId", is(ONE)));
    actions.andExpect(jsonPath("$[0].isDefault", is(true)));
    actions.andExpect(jsonPath("$[0].inboundChannel", is(InboundChannel.ONLINE.name())));
    actions.andExpect(jsonPath("$[0].variant", is(VARIANT_ENG)));
    actions.andExpect(jsonPath("$[0].outboundChannel", is(OutboundChannel.POST.name())));
  }

  private ActionPlanMapping buildActionPlanMapping() {
    ActionPlanMapping actionPlanMapping = new ActionPlanMapping();
    actionPlanMapping.setActionPlanMappingId(ONE);
    actionPlanMapping.setActionPlanId(ONE);
    actionPlanMapping.setCaseTypeId(ONE);
    actionPlanMapping.setIsDefault(true);
    actionPlanMapping.setInboundChannel(InboundChannel.ONLINE);
    actionPlanMapping.setVariant(VARIANT_ENG);
    actionPlanMapping.setOutboundChannel(OutboundChannel.POST);
    return actionPlanMapping;
  }

}
