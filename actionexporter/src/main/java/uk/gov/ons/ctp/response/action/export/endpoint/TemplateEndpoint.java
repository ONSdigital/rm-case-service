package uk.gov.ons.ctp.response.action.export.endpoint;

import java.io.InputStream;
import java.net.URI;
import java.util.List;

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

import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.representation.TemplateDocumentDTO;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;

/**
 * The REST endpoint controller for Templates.
 */
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

  /**
   * To retrieve all Templates
   * 
   * @return a list of Templates
   */
  @GET
  @Path("/")
  public List<TemplateDocumentDTO> findAllTemplates() {
    log.debug("Entering findAllTemplates ...");
    List<TemplateDocument> templateDocuments = templateService.retrieveAllTemplateDocuments();
    List<TemplateDocumentDTO> results = mapperFacade.mapAsList(templateDocuments, TemplateDocumentDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  /**
   * To retrieve a specific Template
   * 
   * @param templateName for the specific Template to retrieve
   * @return the specific Template
   * @throws CTPException if no Template found
   */
  @GET
  @Path("/{templateName}")
  public TemplateDocumentDTO findTemplate(@PathParam("templateName") final String templateName) throws CTPException {
    log.debug("Entering findTemplate with {}", templateName);
    TemplateDocument result = templateService.retrieveTemplateDocument(templateName);
    return mapperFacade.map(result, TemplateDocumentDTO.class);
  }

  /**
   * To store a Template
   * 
   * @param templateName the Template name
   * @param fileContents the Template content
   * @return 201 if created
   * @throws CTPException if the Template can't be stored
   */
  @POST
  @Path("/{templateName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response storeTemplate(@PathParam("templateName") final String templateName,
      @FormDataParam("file") InputStream fileContents) throws CTPException {
    log.debug("Entering storeTemplate with templateName {}", templateName);
    TemplateDocument templateDocument = templateService.storeTemplateDocument(templateName, fileContents);

    UriBuilder ub = uriInfo.getAbsolutePathBuilder();
    URI templateDocumentUri = ub.build();
    TemplateDocumentDTO templateDocumentDTO = mapperFacade.map(templateDocument, TemplateDocumentDTO.class);
    return Response.created(templateDocumentUri).entity(templateDocumentDTO).build();
  }
}
