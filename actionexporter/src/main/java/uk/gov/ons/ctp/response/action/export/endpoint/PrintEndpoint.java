package uk.gov.ons.ctp.response.action.export.endpoint;

import javax.ws.rs.Consumes;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;

/**
 * The REST endpoint controller for Actions.
 */
@Path("/actions")
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public final class PrintEndpoint implements CTPEndpoint {

//  @Inject
//  private PrintService printService;
//
//  @Inject
//  private MapperFacade mapperFacade;

}
