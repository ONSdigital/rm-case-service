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
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@Path("/freemarker")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ContentEndpoint {
  @Inject
  private DocumentService documentService;

  @Inject
  private MapperFacade mapperFacade;

  @GET
  @Path("/")
  public List<ContentDocumentDTO> findAllTemplates() {
    log.debug("Entering findAllTemplates ...");
    List<ContentDocument> templates = documentService.retrieveAllContentDocuments();
    List<ContentDocumentDTO> results = mapperFacade.mapAsList(templates, ContentDocumentDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  @GET
  @Path("/{templateName}")
  public ContentDocumentDTO findTemplate(@PathParam("templateName") final String templateName) throws CTPException {
    log.debug("Entering findTemplate with {}", templateName);
    ContentDocument result = documentService.retrieveContentDocument(templateName);
    if (result == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "FreeMarker template not found for name %s", templateName);
    }
    return mapperFacade.map(result, ContentDocumentDTO.class);
  }

  @POST
  @Path("/{templateName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public ContentDocumentDTO storeFreeMarkerTemplate(@PathParam("templateName") final String templateName,
                                                    @FormDataParam("file") InputStream fileContents)
          throws CTPException {
    log.debug("Entering storeFreeMarkerTemplate with templateName {}", templateName);
    ContentDocument template = documentService.storeContentDocument(templateName, fileContents);
    documentService.clearTemplateCache();
    return mapperFacade.map(template, ContentDocumentDTO.class);
  }
}
