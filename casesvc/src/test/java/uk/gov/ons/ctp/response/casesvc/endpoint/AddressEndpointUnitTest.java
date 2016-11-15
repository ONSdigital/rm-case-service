package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_ESTABLISH_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_HTC;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LAD;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LATITUDE;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LINE1;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LINE2;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LOCALITY;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LONGITUDE;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_LSOA;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_MSOA;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_ORG_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_OUTPUT_AREA;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_REGION_CODE;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_TOWN_NAME;
import static uk.gov.ons.ctp.response.casesvc.utility.AddressBuilder.ADDRESS_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_NON_EXISTING_POSTCODE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_NON_EXISTING_UPRN;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_POSTCODE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_UPRN;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.ADDRESS_WITH_UPRN_CHECKED_EXCEPTION;
import static uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory.OUR_EXCEPTION_MESSAGE;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.service.AddressService;
import uk.gov.ons.ctp.response.casesvc.utility.MockAddressServiceFactory;

/**
 * Unit Tests for the Address Endpoint
 */
public final class AddressEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(AddressEndpoint.class, AddressService.class, MockAddressServiceFactory.class,
        new CaseSvcBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findByUprnPositiveScenario() {
    with("http://localhost:9998/addresses/%s", ADDRESS_UPRN)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.uprn", ADDRESS_UPRN.intValue())
        .assertStringInBody("$.type", ADDRESS_TYPE)
        .assertStringInBody("$.organisationName", ADDRESS_ORG_NAME)
        .assertStringInBody("$.locality", ADDRESS_LOCALITY)
        .assertStringInBody("$.estabType", ADDRESS_ESTABLISH_TYPE)
        .assertStringInBody("$.line1", ADDRESS_LINE1)
        .assertStringInBody("$.line2", ADDRESS_LINE2)
        .assertStringInBody("$.townName", ADDRESS_TOWN_NAME)
        .assertStringInBody("$.outputArea", ADDRESS_OUTPUT_AREA)
        .assertStringInBody("$.lsoaArea", ADDRESS_LSOA)
        .assertStringInBody("$.msoaArea", ADDRESS_MSOA)
        .assertStringInBody("$.ladCode", ADDRESS_LAD)
        .assertStringInBody("$.regionCode", ADDRESS_REGION_CODE)
        .assertIntegerInBody("$.htc", ADDRESS_HTC)
        .assertDoubleInBody("$.latitude", ADDRESS_LATITUDE)
        .assertDoubleInBody("$.longitude", ADDRESS_LONGITUDE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findByUprnScenarioNotFound() {
    with("http://localhost:9998/addresses/%s", ADDRESS_NON_EXISTING_UPRN)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message",
            String.format("No addresses found for uprn %s", ADDRESS_NON_EXISTING_UPRN))
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findByUprnScenarioThrowCheckedException() {
    with("http://localhost:9998/addresses/%s", ADDRESS_WITH_UPRN_CHECKED_EXCEPTION)
        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
        .assertStringInBody("$.error.code", CTPException.Fault.SYSTEM_ERROR.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message", OUR_EXCEPTION_MESSAGE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findByPostcodePositiveScenario() {
    with("http://localhost:9998/addresses/postcode/%s", ADDRESS_POSTCODE)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(1)
        .assertStringListInBody("$..postcode", ADDRESS_POSTCODE)
        .assertStringListInBody("$..type", ADDRESS_TYPE)
        .assertStringListInBody("$..estabType", ADDRESS_ESTABLISH_TYPE)
        .assertStringListInBody("$..line1", ADDRESS_LINE1)
        .assertStringListInBody("$..line2", ADDRESS_LINE2)
        .assertStringListInBody("$..townName", ADDRESS_TOWN_NAME)
        .assertStringListInBody("$..outputArea", ADDRESS_OUTPUT_AREA)
        .assertStringListInBody("$..lsoaArea", ADDRESS_LSOA)
        .assertStringListInBody("$..msoaArea", ADDRESS_MSOA)
        .assertStringListInBody("$..ladCode", ADDRESS_LAD)
        .assertStringListInBody("$..regionCode", ADDRESS_REGION_CODE)
        .assertIntegerListInBody("$..htc", ADDRESS_HTC)
        .assertDoubleListInBody("$..latitude", ADDRESS_LATITUDE)
        .assertDoubleListInBody("$..longitude", ADDRESS_LONGITUDE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findByPostcodeScenarioNotFound() {
    with("http://localhost:9998/addresses/postcode/%s", ADDRESS_NON_EXISTING_POSTCODE)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message",
            String.format("No addresses found for postcode %s",
                ADDRESS_NON_EXISTING_POSTCODE))
        .andClose();
  }


}
