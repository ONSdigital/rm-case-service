package uk.gov.ons.ctp.response.action.export.endpoint;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.action.ActionExporterBeanMapper;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.export.utility.MockActionRequestServiceFactory;
import uk.gov.ons.ctp.response.action.export.utility.MockSftpServicePublisherFactory;
import uk.gov.ons.ctp.response.action.export.utility.MockTransformationServiceFactory;

import javax.ws.rs.core.Application;

import static uk.gov.ons.ctp.response.action.export.endpoint.ActionRequestEndpoint.ACTION_REQUEST_NOT_FOUND;
import static uk.gov.ons.ctp.response.action.export.utility.MockActionRequestServiceFactory.NON_EXISTING_ACTION_ID;

/**
 * ActionRequestEndpoint unit tests
 */
public class ActionRequestEndpointTest extends CTPJerseyTest {
  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(ActionRequestEndpoint.class,
            new ServiceFactoryPair [] {
                    new ServiceFactoryPair(ActionRequestService.class, MockActionRequestServiceFactory.class),
                    new ServiceFactoryPair(TransformationService.class, MockTransformationServiceFactory.class),
                    new ServiceFactoryPair(SftpServicePublisher.class, MockSftpServicePublisherFactory.class)
            },
            new ActionExporterBeanMapper());
  }

  @Test
  public void findAllActionRequests() {
    with("http://localhost:9998/actionrequests/")
            .assertResponseCodeIs(HttpStatus.OK)
            .assertArrayLengthInBodyIs(3)
            .assertIntegerListInBody("$..actionId", 0, 1, 2)
            .andClose();
  }

  @Test
  public void findNonExistingActionRequest() {
    with("http://localhost:9998/actionrequests/%s/", NON_EXISTING_ACTION_ID)
            .assertResponseCodeIs(HttpStatus.NOT_FOUND)
            .assertFaultIs(CTPException.Fault.RESOURCE_NOT_FOUND)
            .assertTimestampExists()
            .assertMessageEquals(String.format("%s %d", ACTION_REQUEST_NOT_FOUND, NON_EXISTING_ACTION_ID))
            .andClose();
  }
}
