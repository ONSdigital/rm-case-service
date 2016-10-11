package uk.gov.ons.ctp.response.action.export.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.util.CollectionUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;
import uk.gov.ons.ctp.response.action.export.service.DocumentService;
import uk.gov.ons.ctp.response.action.representation.ContentDocumentDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Path("/content")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ContentEndpoint {
  @Inject
  private DocumentService documentService;

  @Inject
  private MapperFacade mapperFacade;

  @Context
  private UriInfo uriInfo;

  @GET
  @Path("/")
  public List<ContentDocumentDTO> findAllContentDocuments() {
    log.debug("Entering findAllContentDocuments ...");
    List<ContentDocument> contentDocuments = documentService.retrieveAllContentDocuments();
    List<ContentDocumentDTO> results = mapperFacade.mapAsList(contentDocuments, ContentDocumentDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  @GET
  @Path("/{contentDocumentName}")
  public ContentDocumentDTO findContentDocument(@PathParam("contentDocumentName") final String contentDocumentName)
          throws CTPException {
    log.debug("Entering findContentDocument with {}", contentDocumentName);
    ContentDocument result = documentService.retrieveContentDocument(contentDocumentName);
    if (result == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "ContentDocument not found for name %s", contentDocumentName);
    }
    return mapperFacade.map(result, ContentDocumentDTO.class);
  }

  @POST
  @Path("/{contentDocumentName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response storeContentDocument(@PathParam("contentDocumentName") final String contentDocumentName,
                                       @FormDataParam("file") InputStream fileContents)
          throws CTPException {
    log.debug("Entering storeContentDocument with contentDocumentName {}", contentDocumentName);
    ContentDocument contentDocument = documentService.storeContentDocument(contentDocumentName, fileContents);
    documentService.clearTemplateCache();

    UriBuilder ub = uriInfo.getAbsolutePathBuilder();
    URI contentDocumentUri = ub.path(contentDocumentName).build();
    ContentDocumentDTO contentDocumentDTO = mapperFacade.map(contentDocument, ContentDocumentDTO.class);
    return Response.created(contentDocumentUri).entity(contentDocumentDTO).build();
  }
}
