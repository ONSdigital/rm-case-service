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
 * Created by philippe.brossier on 2/23/16.
 */
public class MsoaEndpointUnitTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(MsoaEndpoint.class, MsoaService.class, MockMsoaServiceFactory.class, new CaseFrameBeanMapper());
  }

  @Test
  public void findByIdPositiveScenario() {
    with("http://localhost:9998/msoas/%s", MSOA_WITH_CODE_MSOA123)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertStringInBody("$.msoaCode", MSOA_WITH_CODE_MSOA123)
            .assertStringInBody("$.msoaName", String.format("%s%s", MSOA_WITH_CODE_MSOA123, NAME))
            .assertStringInBody("$.ladCode", LAD_FOR_MSOA123)
            .andClose();
  }

  @Test
  public void findByIdScenarioNotFound() {
    with("http://localhost:9998/msoas/%s", MSOA_WITH_NON_EXISTING_CODE)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", String.format("MSOA not found for id %s", MSOA_WITH_NON_EXISTING_CODE))
            .andClose();
  }


  @Test
  public void findByIdScenarioThrowCheckedException() {
    with("http://localhost:9998/msoas/%s", MSOA_WITH_CODE_CHECKED_EXCEPTION)
            .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
            .assertStringInBody("$.error.code", CTPException.Fault.SYSTEM_ERROR.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", MockMsoaServiceFactory.OUR_EXCEPTION_MESSAGE)
            .andClose();
  }

  @Test
  public void findAllAddressSummariesForMsoaIdPositiveScenario() {
    with("http://localhost:9998/msoas/%s/addresssummaries", MSOA_WITH_CODE_MSOA123)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertArrayLengthInBodyIs(2)
            .assertStringListInBody("$..addressType", ADDRESS_SUMMARY1_TYPE, ADDRESS_SUMMARY2_TYPE)
            .assertIntegerListInBody("$..uprn", new Integer(ADDRESS_SUMMARY1_UPRN.intValue()), new Integer(ADDRESS_SUMMARY2_UPRN.intValue()))
            .andClose();
  }


  @Test
  public void findAllAddressSummariesForMsoaIdScenario204() {
     with("http://localhost:9998/msoas/%s/addresssummaries", MSOA_WITH_CODE_204)
            .assertResponseCodeIs(HttpStatus.NO_CONTENT)
            .andClose();
  }
}