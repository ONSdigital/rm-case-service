package uk.gov.ons.ctp.response.action.export.endpoint;


import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.util.CollectionUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;
import uk.gov.ons.ctp.response.action.export.representation.TemplateMappingDocumentDTO;

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
import java.net.URI;
import java.util.List;

/**
 * The REST endpoint controller for TemplateMappings.
 */
@Path("/templatemappings")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class TemplateMappingEndpoint {
  @Inject
  private TemplateMappingService templateMappingService;

  @Inject
  private MapperFacade mapperFacade;

  @Context
  private UriInfo uriInfo;

  /**
   * To retrieve all TemplateMappings
   * @return a list of TemplateMappings
   */
  @GET
  @Path("/")
  public List<TemplateMappingDocumentDTO> findAllTemplateMappings() {
    log.debug("Entering findAllTemplateMappings ...");
    List<TemplateMappingDocument> templateMappingDocuments =
            templateMappingService.retrieveAllTemplateMappingDocuments();
    List<TemplateMappingDocumentDTO> results =
            mapperFacade.mapAsList(templateMappingDocuments, TemplateMappingDocumentDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  /**
   * To retrieve a specific TemplateMapping
   * @param templateMappingName for the specific TemplateMapping to retrieve
   * @return the specific TemplateMapping
   * @throws CTPException if no TemplateMapping found
   */
  @GET
  @Path("/{templateMappingName}")
  public TemplateMappingDocumentDTO findTemplateMapping(@PathParam("templateMappingName") final String
                                                                  templateMappingName) throws CTPException {
    log.debug("Entering findTemplateMapping with {}", templateMappingName);
    TemplateMappingDocument result = templateMappingService.retrieveTemplateMappingDocument(templateMappingName);
    if (result == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Template Mapping not found for name %s",
              templateMappingName);
    }
    return mapperFacade.map(result, TemplateMappingDocumentDTO.class);
  }

  /**
   * To store a TemplateMapping
   * @param templateMappingName the TemplateMapping name
   * @param fileContents the TemplateMapping content
   * @return 201 if created
   * @throws CTPException if the TemplateMapping can't be stored
   */
  @POST
  @Path("/{templateMappingName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public Response storeTemplateMapping(@PathParam("templateMappingName") final String templateMappingName,
                                @FormDataParam("file") InputStream fileContents) throws CTPException {
    log.debug("Entering storeTemplateMapping with templateMappingName {}", templateMappingName);
    TemplateMappingDocument templateMappingDocument = templateMappingService.storeTemplateMappingDocument(
            templateMappingName, fileContents);

    UriBuilder ub = uriInfo.getAbsolutePathBuilder();
    URI templateMappingDocumentUri = ub.build();
    TemplateMappingDocumentDTO templateMappingDocumentDTO = mapperFacade.map(templateMappingDocument,
            TemplateMappingDocumentDTO.class);
    return Response.created(templateMappingDocumentUri).entity(templateMappingDocumentDTO).build();
  }
}
