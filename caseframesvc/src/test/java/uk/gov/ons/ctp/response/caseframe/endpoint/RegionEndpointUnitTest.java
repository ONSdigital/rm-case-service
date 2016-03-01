package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.LAD1_CODE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.LAD1_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.LAD2_CODE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.LAD2_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.OUR_EXCEPTION_MESSAGE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION1_CODE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION1_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION2_CODE;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION2_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION_WITH_CODE_204;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION_WITH_CODE_CHECKED_EXCEPTION;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION_WITH_CODE_REG123;
import static uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory.REGION_WITH_NON_EXISTING_CODE;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.service.RegionService;
import uk.gov.ons.ctp.response.caseframe.utility.MockRegionServiceFactory;

/**
 * Created by philippe.brossier on 2/16/16.
 */
public class RegionEndpointUnitTest extends CTPJerseyTest {

  @Override
  public Application configure() {
    return super.init(RegionEndpoint.class, RegionService.class, MockRegionServiceFactory.class, new CaseFrameBeanMapper());
  }

  @Test
  public void findAllPositiveScenario() {
    with("http://localhost:9998/regions")
            .assertResponseCodeIs(HttpStatus.OK)
            .assertArrayLengthInBodyIs(2)
            .assertStringListInBody("$..regionCode", REGION1_CODE, REGION2_CODE)
            .assertStringListInBody("$..regionName", REGION1_NAME, REGION2_NAME)
            .andClose();
  }

  @Test
  public void findByIdPositiveScenario() {
    with("http://localhost:9998/regions/%s", REGION_WITH_CODE_REG123)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertStringInBody("$.regionCode", REGION_WITH_CODE_REG123)
            .assertStringInBody("$.regionName", String.format("%s%s", REGION_WITH_CODE_REG123, NAME))
            .andClose();
  }

  @Test
  public void findByIdScenarioNotFound() {
    with("http://localhost:9998/regions/%s", REGION_WITH_NON_EXISTING_CODE)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertStringInBody("$.error.code", CTPException.Fault.RESOURCE_NOT_FOUND.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", String.format("Region not found for id %s", REGION_WITH_NON_EXISTING_CODE))
            .andClose();
  }

  @Test
  public void findByIdScenarioThrowCheckedException() {
    with("http://localhost:9998/regions/%s", REGION_WITH_CODE_CHECKED_EXCEPTION)
            .assertResponseCodeIs(HttpStatus.INTERNAL_SERVER_ERROR)
            .assertStringInBody("$.error.code", CTPException.Fault.SYSTEM_ERROR.toString())
            .assertTimestampExists()
            .assertStringInBody("$.error.message", OUR_EXCEPTION_MESSAGE)
            .andClose();
  }

  @Test
  public void findAllLadsForRegionIdPositiveScenario() {
    with("http://localhost:9998/regions/%s/lads", REGION_WITH_CODE_REG123)
            .assertResponseCodeIs(HttpStatus.OK)
            .assertArrayLengthInBodyIs(2)
            .assertStringListInBody("$..ladCode", LAD1_CODE, LAD2_CODE)
            .assertStringListInBody("$..ladName", LAD1_NAME, LAD2_NAME)
            .assertStringListInBody("$..regionCode", REGION_WITH_CODE_REG123, REGION_WITH_CODE_REG123)
            .andClose();
  }

  @Test
  public void findAllLadsForRegionIdScenario204() {
    with("http://localhost:9998/regions/%s/lads", REGION_WITH_CODE_204)
            .assertResponseCodeIs(HttpStatus.NO_CONTENT)
            .andClose();
  }

}
