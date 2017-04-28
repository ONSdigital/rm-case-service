package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.*;
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
import uk.gov.ons.ctp.response.casesvc.domain.model.Address;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;
import uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Unit Tests for the Address Endpoint
 */
public final class AddressEndpointUnitTest {

  @InjectMocks
  private AddressEndpoint addressEndpoint;

  @Mock
  private AddressService addressService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;

  private static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(addressEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .build();
  }

  /**
   * a test
   */
  @Test
  public void findByUprnPositiveScenario() throws Exception {
    when(addressService.findByUprn(ADDRESS_UPRN)).thenReturn(AddressBuilder.address().uprn(ADDRESS_UPRN).buildAddress());

    ResultActions actions = mockMvc.perform(getJson(String.format("/addresses/%s", ADDRESS_UPRN)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(AddressEndpoint.class));
    actions.andExpect(handler().methodName("findAddressesByUprn"));
    actions.andExpect(jsonPath("$.uprn", is(ADDRESS_UPRN.intValue())));
    actions.andExpect(jsonPath("$.type", is(ADDRESS_TYPE)));
    actions.andExpect(jsonPath("$.organisationName", is(ADDRESS_ORG_NAME)));
    actions.andExpect(jsonPath("$.locality", is(ADDRESS_LOCALITY)));
    actions.andExpect(jsonPath("$.estabType", is(ADDRESS_ESTABLISH_TYPE)));
    actions.andExpect(jsonPath("$.line1", is(ADDRESS_LINE1)));
    actions.andExpect(jsonPath("$.line2", is(ADDRESS_LINE2)));
    actions.andExpect(jsonPath("$.townName", is(ADDRESS_TOWN_NAME)));
    actions.andExpect(jsonPath("$.outputArea", is(ADDRESS_OUTPUT_AREA)));
    actions.andExpect(jsonPath("$.lsoaArea", is(ADDRESS_LSOA)));
    actions.andExpect(jsonPath("$.msoaArea", is(ADDRESS_MSOA)));
    actions.andExpect(jsonPath("$.ladCode", is(ADDRESS_LAD)));
    actions.andExpect(jsonPath("$.regionCode", is(ADDRESS_REGION_CODE)));
    actions.andExpect(jsonPath("$.htc", is(ADDRESS_HTC)));
    actions.andExpect(jsonPath("$.latitude", is(ADDRESS_LATITUDE)));
    actions.andExpect(jsonPath("$.longitude", is(ADDRESS_LONGITUDE)));
  }

  /**
   * a test
   */
  @Test
  public void findByUprnScenarioNotFound() throws Exception {
    when(addressService.findByUprn(ADDRESS_NON_EXISTING_UPRN)).thenReturn(null);

    ResultActions actions = mockMvc.perform(getJson(String.format("/addresses/%s", ADDRESS_NON_EXISTING_UPRN)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(AddressEndpoint.class));
    actions.andExpect(handler().methodName("findAddressesByUprn"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("No addresses found for uprn %s", ADDRESS_NON_EXISTING_UPRN))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test
   */
  @Test
  public void findByUprnScenarioThrowCheckedException() throws Exception {
    when(addressService.findByUprn(ADDRESS_WITH_UPRN_CHECKED_EXCEPTION)).thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    ResultActions actions = mockMvc.perform(getJson(String.format("/addresses/%s", ADDRESS_WITH_UPRN_CHECKED_EXCEPTION)));

    actions.andExpect(status().is5xxServerError());
    actions.andExpect(handler().handlerType(AddressEndpoint.class));
    actions.andExpect(handler().methodName("findAddressesByUprn"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.SYSTEM_ERROR.name())));
    actions.andExpect(jsonPath("$.error.message", is(OUR_EXCEPTION_MESSAGE)));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * a test
   */
  @Test
  public void findByPostcodePositiveScenario() throws Exception {
    List<Address> result = new ArrayList<>();
    result.add(AddressBuilder.address().postcode(ADDRESS_POSTCODE).buildAddress());
    when(addressService.findByPostcode(ADDRESS_POSTCODE)).thenReturn(result);

    ResultActions actions = mockMvc.perform(getJson(String.format("/addresses/postcode/%s", ADDRESS_POSTCODE)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(AddressEndpoint.class));
    actions.andExpect(handler().methodName("findAddressesByPostcode"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$[0].postcode", is(ADDRESS_POSTCODE)));
    actions.andExpect(jsonPath("$[0].type", is(ADDRESS_TYPE)));
    actions.andExpect(jsonPath("$[0].estabType", is(ADDRESS_ESTABLISH_TYPE)));
    actions.andExpect(jsonPath("$[0].line1", is(ADDRESS_LINE1)));
    actions.andExpect(jsonPath("$[0].line2", is(ADDRESS_LINE2)));
    actions.andExpect(jsonPath("$[0].townName", is(ADDRESS_TOWN_NAME)));
    actions.andExpect(jsonPath("$[0].outputArea", is(ADDRESS_OUTPUT_AREA)));
    actions.andExpect(jsonPath("$[0].lsoaArea", is(ADDRESS_LSOA)));
    actions.andExpect(jsonPath("$[0].msoaArea", is(ADDRESS_MSOA)));
    actions.andExpect(jsonPath("$[0].ladCode", is(ADDRESS_LAD)));
    actions.andExpect(jsonPath("$[0].regionCode", is(ADDRESS_REGION_CODE)));
    actions.andExpect(jsonPath("$[0].htc", is(ADDRESS_HTC)));
    actions.andExpect(jsonPath("$[0].latitude", is(ADDRESS_LATITUDE)));
    actions.andExpect(jsonPath("$[0].longitude", is(ADDRESS_LONGITUDE)));
  }

  /**
   * a test
   */
  @Test
  public void findByPostcodeScenarioNotFound() throws Exception {
    when(addressService.findByPostcode(ADDRESS_NON_EXISTING_POSTCODE)).thenReturn(null);

    ResultActions actions = mockMvc.perform(getJson(String.format("/addresses/postcode/%s", ADDRESS_NON_EXISTING_POSTCODE)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(AddressEndpoint.class));
    actions.andExpect(handler().methodName("findAddressesByPostcode"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("No addresses found for postcode %s",
            ADDRESS_NON_EXISTING_POSTCODE))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }
}
