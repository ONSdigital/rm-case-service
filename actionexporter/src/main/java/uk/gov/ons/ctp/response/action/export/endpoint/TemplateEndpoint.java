package uk.gov.ons.ctp.response.action.export.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.util.CollectionUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;
import uk.gov.ons.ctp.response.action.representation.TemplateDocumentDTO;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.InputStream;
import java.net.URI;
import java.util.List;

@Path("/templates")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TemplateEndpoint {
  @Inject
  private TemplateService templateService;

  @Inject
  private MapperFacade mapperFacade;

  @Context
  private UriInfo uriInfo;

  @GET
  @Path("/")
  public List<TemplateDocumentDTO> findAllTemplates() {
    log.debug("Entering findAllTemplates ...");
    List<TemplateDocument> templateDocuments = templateService.retrieveAllTemplateDocuments();
    List<TemplateDocumentDTO> results = mapperFacade.mapAsList(templateDocuments, TemplateDocumentDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  @GET
  @Path("/{templateName}")
  public TemplateDocumentDTO findTemplate(@PathParam("templateName") final String templateName) throws CTPException {
    log.debug("Entering findTemplate with {}", templateName);
    TemplateDocument result = templateService.retrieveTemplateDocument(templateName);
    if (result == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Template not found for name %s", templateName);
    }
    return mapperFacade.map(result, TemplateDocumentDTO.class);
  }

  @POST
  @Path("/{templateName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response storeTemplate(@PathParam("templateName") final String templateName,
                                @FormDataParam("file") InputStream fileContents) throws CTPException {
    log.debug("Entering storeTemplate with templateName {}", templateName);
    TemplateDocument templateDocument = templateService.storeTemplateDocument(templateName, fileContents);
    templateService.clearTemplateCache();

    UriBuilder ub = uriInfo.getAbsolutePathBuilder();
    URI templateDocumentUri = ub.path(templateName).build();
    TemplateDocumentDTO templateDocumentDTO = mapperFacade.map(templateDocument, TemplateDocumentDTO.class);
    return Response.created(templateDocumentUri).entity(templateDocumentDTO).build();
  }
}
