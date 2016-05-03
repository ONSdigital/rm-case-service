package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.ADDRESS_SUMMARY1_TYPE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.ADDRESS_SUMMARY1_UPRN;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.ADDRESS_SUMMARY2_TYPE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.ADDRESS_SUMMARY2_UPRN;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.LAD_FOR_MSOA123;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.MSOA_WITH_CODE_204;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.MSOA_WITH_CODE_CHECKED_EXCEPTION;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.MSOA_WITH_CODE_MSOA123;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.MSOA_WITH_NON_EXISTING_CODE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory.NAME;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.service.MsoaService;
import uk.gov.ons.ctp.response.caseframe.utility.MockMsoaServiceFactory;

/**
 * A set of tests for the MSOA endpoint
 */
public final class MsoaEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(MsoaEndpoint.class, MsoaService.class, MockMsoaServiceFactory.class, new CaseFrameBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findByIdPositiveScenario() {
    with("http://localhost:9998/msoas/%s", MSOA_WITH_CODE_MSOA123)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertStringInBody("$.code", MSOA_WITH_CODE_MSOA123)
        .assertStringInBody("$.name", String.format("%s%s", MSOA_WITH_CODE_MSOA123, NAME))
        .assertStringInBody("$.ladCode", LAD_FOR_MSOA123)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findByIdScenarioNotFound() {
    with("http://localhost:9998/msoas/%s", MSOA_WITH_NON_EXISTING_CODE)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message", String.format("MSOA not found for id %s", MSOA_WITH_NON_EXISTING_CODE))
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findByIdScenarioThrowCheckedException() {
    with("http://localhost:9998/msoas/%s", MSOA_WITH_CODE_CHECKED_EXCEPTION)
        .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
        .assertStringInBody("$.error.code", CTPException.Fault.SYSTEM_ERROR.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message", MockMsoaServiceFactory.OUR_EXCEPTION_MESSAGE)
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findAllAddressSummariesForMsoaIdPositiveScenario() {
    with("http://localhost:9998/msoas/%s/addresssummaries", MSOA_WITH_CODE_MSOA123)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(2)
        .assertStringListInBody("$..type", ADDRESS_SUMMARY1_TYPE, ADDRESS_SUMMARY2_TYPE)
        .assertIntegerListInBody("$..uprn", new Integer(ADDRESS_SUMMARY1_UPRN.intValue()),
            new Integer(ADDRESS_SUMMARY2_UPRN.intValue()))
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findAllAddressSummariesForMsoaIdScenario204() {
    with("http://localhost:9998/msoas/%s/addresssummaries", MSOA_WITH_CODE_204)
        .assertResponseCodeIs(HttpStatus.NO_CONTENT)
        .andClose();
  }

  /*ss
   * a test
   */
  @Test
  public void findAllAddressSummariesForNonExistingMsoa() {
    with("http://localhost:9998/msoas/%s/addresssummaries", MSOA_WITH_NON_EXISTING_CODE)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
        .assertTimestampExists()
        .assertStringInBody("$.error.message", String.format("MSOA not found for id %s", MSOA_WITH_NON_EXISTING_CODE))
        .andClose();
  }
}
