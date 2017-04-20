package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.core.Is.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.CASE_TYPE_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.MAPPING_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.NON_EXISTENT_MAPPING_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.VARIANT_ENG;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.representation.OutboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;
import uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory;

/**
 */
public final class ActionPlanMappingEndpointUnitTest {

  @InjectMocks
  private ActionPlanMappingEndpoint actionPlanMappingEndpoint;

  @Mock
  private ActionPlanMappingService actionPlanMappingService;

  @Mock
  private CaseTypeService caseTypeService;

  private MockMvc mockMvc;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(actionPlanMappingEndpoint)
            .build();

    actionPlanMappingService = MockActionPlanMappingServiceFactory.provide();
    caseTypeService = MockCaseTypeServiceFactory.provide();
  }

  /**
   * a test
   */
  @Test
  public void findMappingFound() throws Exception {
//    with("/actionplanmappings/%s", MAPPING_ID)
//        .assertResponseCodeIs(HttpStatus.OK)

//        .assertIntegerInBody("$.actionPlanMappingId", MAPPING_ID)
//        .assertIntegerInBody("$.actionPlanId", 1)
//        .assertIntegerInBody("$.caseTypeId", 1)
//        .assertBooleanInBody("$.isDefault", true)
//        .assertStringInBody("$.inboundChannel",InboundChannel.ONLINE.name())
//        .assertStringInBody("$.variant", VARIANT_ENG)
//        .assertStringInBody("$.outboundChannel", OutboundChannel.POST.name())
//        .andClose();

    ResultActions actions = mockMvc.perform(getJson(String.format("/actionplanmappings/%s", MAPPING_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(ActionPlanMappingEndpoint.class));
    actions.andExpect(handler().methodName("findActionPlanMappingByActionPlanMappingId"));
    actions.andExpect(jsonPath("$.actionPlanMappingId", is(MAPPING_ID)));
    actions.andExpect(jsonPath("$.actionPlanId", is(1)));
    actions.andExpect(jsonPath("$.caseTypeId", is(1)));
    actions.andExpect(jsonPath("$.isDefault", is(true)));
    actions.andExpect(jsonPath("$.inboundChannel", is(InboundChannel.ONLINE.name())));
    actions.andExpect(jsonPath("$.variant", is(VARIANT_ENG)));
    actions.andExpect(jsonPath("$.outboundChannel", is(OutboundChannel.POST.name())));
  }

//  /**
//   * a test
//   */
//  @Test
//  public void findMappingNotFound() {
//    with("/actionplanmappings/%s", NON_EXISTENT_MAPPING_ID)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
//        .assertTimestampExists()
//        .assertMessageEquals("ActionPlanMapping not found for id %s", NON_EXISTENT_MAPPING_ID)
//        .andClose();
//  }
//
//  /**
//   * a test
//   */
//  @Test
//  public void findMappingsByCaseTypeId() {
//    with("/actionplanmappings/casetype/%d", CASE_TYPE_ID)
//        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
//        .assertArrayLengthInBodyIs(1)
//        .assertIntegerOccursThroughoutListInBody("$..actionPlanMappingId", MAPPING_ID)
//        .assertIntegerOccursThroughoutListInBody("$..actionPlanId", 1)
//        .assertIntegerOccursThroughoutListInBody("$..caseTypeId", 1)
//        .assertStringOccursThroughoutListInBody("$..inboundChannel",InboundChannel.ONLINE.name())
//        .assertStringOccursThroughoutListInBody("$..variant", VARIANT_ENG)
//        .assertStringOccursThroughoutListInBody("$..outboundChannel", OutboundChannel.POST.name())
//        .andClose();
//  }
}
