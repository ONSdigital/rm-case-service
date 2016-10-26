package uk.gov.ons.ctp.response.action.export.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.util.CollectionUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;
import uk.gov.ons.ctp.response.action.export.representation.ActionRequestDocumentDTO;
import uk.gov.ons.ctp.response.action.export.representation.TemplateDocumentDTO;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.URI;
import java.util.List;

/**
 * The REST endpoint controller for ActionRequests.
 */
@Path("/actionrequests")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ActionRequestEndpoint {

  public static final String ACTION_REQUEST_NOT_FOUND = "ActionRequest not found for actionId";

  @Inject
  private ActionRequestService actionRequestService;

  @Inject
  private TransformationService transformationService;

  @Inject
  private SftpServicePublisher sftpService;

  @Inject
  private MapperFacade mapperFacade;

  @Context
  private UriInfo uriInfo;

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
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s %d", ACTION_REQUEST_NOT_FOUND, actionId));
    }
    return mapperFacade.map(result, ActionRequestDocumentDTO.class);
  }

  @POST
  @Path("/{actionId}")
  public Response export(@PathParam("actionId") final BigInteger actionId) throws CTPException {
    log.debug("Entering export with actionId {}", actionId);
    ActionRequestDocument actionRequestDocument = actionRequestService.retrieveActionRequestDocument(actionId);
    if (actionRequestDocument == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s %d", ACTION_REQUEST_NOT_FOUND, actionId));
    }

    SftpMessage message = transformationService.processActionRequest(actionRequestDocument);
    // TODO refactor the below - same code than in ExportScheduler
    message.getOutputStreams().forEach((fileName, stream) -> {
      sftpService.sendMessage(fileName, message.getActionRequestIds(fileName), stream);
    });

    UriBuilder ub = uriInfo.getAbsolutePathBuilder();
    URI actionRequestDocumentUri = ub.path(actionId.toString()).build();
    ActionRequestDocumentDTO actionRequestDocumentDTO = mapperFacade.map(actionRequestDocument, ActionRequestDocumentDTO.class);
    return Response.created(actionRequestDocumentUri).entity(actionRequestDocumentDTO).build();
  }
}
