package uk.gov.ons.ctp.response.action.export.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.util.CollectionUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.representation.ActionRequestDocumentDTO;
import uk.gov.ons.ctp.response.action.export.representation.TemplateDocumentDTO;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.math.BigInteger;
import java.util.List;

/**
 * The REST endpoint controller for ActionRequests.
 */
@Path("/actionrequests")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ActionRequestEndpoint {

  @Inject
  private ActionRequestService actionRequestService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * To retrieve all ActionRequests
   * @return a list of ActionRequests
   */
  @GET
  @Path("/")
  public List<ActionRequestDocumentDTO> findAllActionRequests() {
    log.debug("Entering findAllActionRequests ...");
    List<ActionRequestDocument> actionRequestDocuments = actionRequestService.retrieveAllActionRequestDocuments();
    List<ActionRequestDocumentDTO> results = mapperFacade.mapAsList(actionRequestDocuments,
            ActionRequestDocumentDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  /**
   * To retrieve a specific ActionRequest
   * @param actionId for the specific ActionRequest to retrieve
   * @return the specific ActionRequest
   * @throws CTPException if no ActionRequest found
   */
  @GET
  @Path("/{actionId}")
  public ActionRequestDocumentDTO findActionRequest(@PathParam("actionId") final BigInteger actionId)
          throws CTPException {
    log.debug("Entering findActionRequest with {}", actionId);
    ActionRequestDocument result = actionRequestService.retrieveActionRequestDocument(actionId);
    if (result == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "ActionRequest not found for actionId %d",
              actionId);
    }
    return mapperFacade.map(result, ActionRequestDocumentDTO.class);
  }
}
