package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.error.InvalidRequestException;

import javax.validation.Valid;
import java.net.URI;

@RestController
@RequestMapping(value = "/cases/action-template", produces = "application/json")
public class ActionTemplateEndpoint implements CTPEndpoint {
    private static final Logger log = LoggerFactory.getLogger(ActionTemplateEndpoint.class);
    private ActionTemplateRepository actionTemplateRepo;
    private MapperFacade mapperFacade;
    @Autowired
    public ActionTemplateEndpoint(ActionTemplateRepository actionTemplate, MapperFacade mapperFacade) {
        this.actionTemplateRepo = actionTemplate;
        this.mapperFacade = mapperFacade;
    }

    /**
     * Endpoint to register a new action template
     * @param requestActionTemplate  represents the new template
     * @return ResponseEntity
     */
    @RequestMapping(value = "/", method = RequestMethod.POST)
    public ResponseEntity<ActionTemplateDTO> createActionTemplate(
            @RequestBody @Valid final ActionTemplateDTO requestActionTemplate) {
        log.with("Template Name", requestActionTemplate.getName())
                .with("Description", requestActionTemplate.getDescription())
                .debug("Recording a new action template");
        ActionTemplate newTemplate = mapperFacade.map(requestActionTemplate, ActionTemplate.class);
        ActionTemplate createdTemplate = actionTemplateRepo.save(newTemplate);
        ActionTemplateDTO mappedCreatedActionTemplateDTO =
                mapperFacade.map(createdTemplate, ActionTemplateDTO.class);
        String newResourceUrl =
                ServletUriComponentsBuilder.fromCurrentRequest()
                        .buildAndExpand(mappedCreatedActionTemplateDTO.getName())
                        .toUri()
                        .toString();
        return ResponseEntity.created(URI.create(newResourceUrl)).body(mappedCreatedActionTemplateDTO);
    }
}
