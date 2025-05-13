package uk.gov.ons.ctp.response.casesvc.endpoint;

import jakarta.validation.Valid;
import java.net.URI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionTemplateRepository;

@RestController
@RequestMapping(value = "/action-template", produces = "application/json")
public class CaseActionTemplateEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseActionTemplateEndpoint.class);
  @Autowired private CaseActionTemplateRepository actionTemplateRepo;

  @Autowired
  public CaseActionTemplateEndpoint(CaseActionTemplateRepository actionTemplate) {
    this.actionTemplateRepo = actionTemplate;
  }

  /**
   * Endpoint to register a new action template
   *
   * @param requestActionTemplate represents the new template
   * @return ResponseEntity
   */
  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<CaseActionTemplate> createActionTemplate(
      @RequestBody @Valid final CaseActionTemplate requestActionTemplate) {
    log.with("templateName", requestActionTemplate.getType())
        .with("description", requestActionTemplate.getDescription())
        .debug("Recording a new action template");
    CaseActionTemplate createdTemplate = actionTemplateRepo.save(requestActionTemplate);
    return ResponseEntity.created(
            URI.create(String.format("/template/%s", createdTemplate.getType())))
        .body(createdTemplate);
  }
}
