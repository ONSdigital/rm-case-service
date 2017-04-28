package uk.gov.ons.ctp.response.casesvc.endpoint;


import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

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
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 */
public final class CaseGroupEndpointUnitTest {

  @InjectMocks
  private CaseGroupEndpoint caseGroupEndpoint;

  @Mock
  private AddressService addressService;

  @Mock
  private CaseGroupService caseGroupService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;

  private static final Integer CASE_GROUP_ID = 1;
  private static final Integer SAMPLE_ID = 2;
  private static final Integer NON_EXISTENT_CASE_GROUP_ID = 99;
  private static final Long ADDRESS_UPRN = 123L;
  private static final Long ADDRESS_UPRN_NO_CASEGROUP = 124L;
  private static final Long ADDRESS_NON_EXISTING_UPRN = 999L;

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(caseGroupEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .build();
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupByCaseGroupIdFound() throws Exception {
    when(caseGroupService.findCaseGroupByCaseGroupId(CASE_GROUP_ID)).thenReturn(CaseGroup.builder().caseGroupId(CASE_GROUP_ID).sampleId(SAMPLE_ID).uprn(ADDRESS_UPRN).build());

    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/%s", CASE_GROUP_ID)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupById"));
    actions.andExpect(jsonPath("$.caseGroupId", is(CASE_GROUP_ID)));
    actions.andExpect(jsonPath("$.sampleId", is(SAMPLE_ID)));
    actions.andExpect(jsonPath("$.uprn", is(ADDRESS_UPRN.intValue())));
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

  /**
   * a test
   */
  @Test
  public void findCaseGroupByUprnNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/uprn/%s", ADDRESS_NON_EXISTING_UPRN)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupsByUprn"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format(CaseGroupEndpoint.ERRORMSG_ADDRESSNOTFOUND + " UPRN %s", ADDRESS_NON_EXISTING_UPRN))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }


  /**
   * a test
   */
  @Test
  public void findCaseGroupsByUprnFound() throws Exception {
    when(addressService.findByUprn(ADDRESS_UPRN)).thenReturn(AddressBuilder.address().uprn(AddressBuilder.ADDRESS_UPRN).buildAddress());
    List<CaseGroup> result = new ArrayList<>();
    result.add(CaseGroup.builder().caseGroupId(CASE_GROUP_ID).sampleId(SAMPLE_ID).uprn(ADDRESS_UPRN).build());
    when(caseGroupService.findCaseGroupsByUprn(ADDRESS_UPRN)).thenReturn(result);

    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/uprn/%s", ADDRESS_UPRN)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CaseGroupEndpoint.class));
    actions.andExpect(handler().methodName("findCaseGroupsByUprn"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$[0].caseGroupId", is(CASE_GROUP_ID)));
    actions.andExpect(jsonPath("$[0].sampleId", is(SAMPLE_ID)));
    actions.andExpect(jsonPath("$[0].uprn", is(ADDRESS_UPRN.intValue())));
  }

  /**
   * a test
   */
  @Test
  public void findCaseGroupsByUprnFoundButNoCaseGroups() throws Exception {
    when(addressService.findByUprn(ADDRESS_UPRN_NO_CASEGROUP)).thenReturn(AddressBuilder.address().uprn(AddressBuilder.ADDRESS_UPRN).buildAddress());
    List<CaseGroup> result = new ArrayList<>();
    when(caseGroupService.findCaseGroupsByUprn(ADDRESS_UPRN_NO_CASEGROUP)).thenReturn(result);

    ResultActions actions = mockMvc.perform(getJson(String.format("/casegroups/uprn/%s", ADDRESS_UPRN_NO_CASEGROUP)));

    actions.andExpect(status().isNoContent());
  }
  
}
