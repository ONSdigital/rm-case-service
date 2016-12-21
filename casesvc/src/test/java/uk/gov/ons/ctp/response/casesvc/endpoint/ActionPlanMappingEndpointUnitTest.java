package uk.gov.ons.ctp.response.casesvc.endpoint;


import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.representation.OutboundChannel;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;
import uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory;
import uk.gov.ons.ctp.response.casesvc.utility.MockCaseTypeServiceFactory;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.MAPPING_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.VARIANT_ENG;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.NON_EXISTENT_MAPPING_ID;
import static uk.gov.ons.ctp.response.casesvc.utility.MockActionPlanMappingServiceFactory.CASE_TYPE_ID;

/**
 */
public final class ActionPlanMappingEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(ActionPlanMappingEndpoint.class,
        new ServiceFactoryPair[] {
            new ServiceFactoryPair(ActionPlanMappingService.class, MockActionPlanMappingServiceFactory.class),
            new ServiceFactoryPair(CaseTypeService.class, MockCaseTypeServiceFactory.class)
        },
        new CaseSvcBeanMapper());
  }

  /**
   * a test
   */
  @Test
  public void findMappingFound() {
    with("/actionplanmappings/%s", MAPPING_ID)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertIntegerInBody("$.actionPlanMappingId", MAPPING_ID)
        .assertIntegerInBody("$.actionPlanId", 1)
        .assertIntegerInBody("$.caseTypeId", 1)
        .assertBooleanInBody("$.isDefault", true)
        .assertStringInBody("$.inboundChannel",InboundChannel.ONLINE.name()) 
        .assertStringInBody("$.variant", VARIANT_ENG)
        .assertStringInBody("$.outboundChannel", OutboundChannel.POST.name())
        .andClose();
  }

  /**
   * a test
   */
  @Test
  public void findMappingNotFound() {
    with("/actionplanmappings/%s", NON_EXISTENT_MAPPING_ID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
        .assertTimestampExists()
        .assertMessageEquals("ActionPlanMapping not found for id %s", NON_EXISTENT_MAPPING_ID)
        .andClose();
  }
  
  /**
   * a test
   */
  @Test
  public void findMappingsByCaseTypeId() {
    with("/actionplanmappings/casetype/%d", CASE_TYPE_ID)
        .assertResponseCodeIs(HttpStatus.NOT_FOUND)
        .assertArrayLengthInBodyIs(1)
        .assertIntegerOccursThroughoutListInBody("$..actionPlanMappingId", MAPPING_ID)
        .assertIntegerOccursThroughoutListInBody("$..actionPlanId", 1)
        .assertIntegerOccursThroughoutListInBody("$..caseTypeId", 1)
        .assertStringOccursThroughoutListInBody("$..inboundChannel",InboundChannel.ONLINE.name()) 
        .assertStringOccursThroughoutListInBody("$..variant", VARIANT_ENG)
        .assertStringOccursThroughoutListInBody("$..outboundChannel", OutboundChannel.POST.name())
        .andClose();
  }
}
