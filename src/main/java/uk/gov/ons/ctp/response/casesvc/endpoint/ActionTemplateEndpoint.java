package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;

import javax.validation.Valid;

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

    @PostMapping("/")
    public ActionTemplate createActionTemplate(
            @RequestBody @Valid final ActionTemplateDTO requestActionTemplate) {
        log.with("Template Name", requestActionTemplate.getName())
                .with("Description", requestActionTemplate.getDescription())
                .debug("Recording a new action template");
        ActionTemplate newTemplate = mapperFacade.map(requestActionTemplate, ActionTemplate.class);
        return actionTemplateRepo.save(newTemplate);
    }
}
