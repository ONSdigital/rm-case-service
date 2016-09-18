package uk.gov.ons.ctp.response.action.export.templating.freemarker.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.springframework.util.CollectionUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.representation.FreeMarkerTemplateDTO;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.service.FreeMarkerService;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.io.InputStream;
import java.util.List;

@Path("/freemarker")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class FreeMarkerEndpoint {
  @Inject
  private FreeMarkerService freeMarkerService;

  @Inject
  private MapperFacade mapperFacade;

  @GET
  @Path("/")
  public List<FreeMarkerTemplateDTO> findAllTemplates() {
    log.debug("Entering findAllTemplates ...");
    List<FreeMarkerTemplate> templates = freeMarkerService.retrieveAllTemplates();
    List<FreeMarkerTemplateDTO> results = mapperFacade.mapAsList(templates, FreeMarkerTemplateDTO.class);
    return CollectionUtils.isEmpty(results) ? null : results;
  }

  @GET
  @Path("/{templateName}")
  public FreeMarkerTemplateDTO findTemplate(@PathParam("templateName") final String templateName) throws CTPException {
    log.debug("Entering findTemplate with {}", templateName);
    FreeMarkerTemplate result = freeMarkerService.retrieveTemplate(templateName);
    if (result == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "FreeMarker template not found for name %s", templateName);
    }
    return mapperFacade.map(result, FreeMarkerTemplateDTO.class);
  }

  @POST
  @Path("/{templateName}")
  @Consumes(MediaType.MULTIPART_FORM_DATA)
  public FreeMarkerTemplateDTO storeFreeMarkerTemplate(@PathParam("templateName") final String templateName,
                                                       @FormDataParam("file") InputStream fileContents)
          throws CTPException {
    log.debug("Entering storeFreeMarkerTemplate with templateName {}", templateName);
    FreeMarkerTemplate template = freeMarkerService.storeTemplate(templateName, fileContents);
    //TODO configuration.clearTemplateCache();
    return mapperFacade.map(template, FreeMarkerTemplateDTO.class);
  }
}
