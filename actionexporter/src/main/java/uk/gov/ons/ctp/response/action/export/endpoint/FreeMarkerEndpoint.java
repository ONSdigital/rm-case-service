package uk.gov.ons.ctp.response.action.export.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.representation.FreeMarkerTemplateDTO;
import uk.gov.ons.ctp.response.action.export.service.FreeMarkerService;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.*;

@Path("/freemarker")
@Produces({"application/json"})
@Slf4j
public class FreeMarkerEndpoint {
  @Inject
  private FreeMarkerService freeMarkerService;

  @Inject
  private MapperFacade mapperFacade;

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
  public FreeMarkerTemplateDTO storeFreeMarkerTemplate(@PathParam("templateName") final String templateName,
                                                       @RequestParam("template") MultipartFile file)
          throws CTPException {
    log.debug("Entering storeFreeMarkerTemplate with templateName {}", templateName);
    FreeMarkerTemplate template = freeMarkerService.storeTemplate(templateName, file);
    return mapperFacade.map(template, FreeMarkerTemplateDTO.class);
  }
}
