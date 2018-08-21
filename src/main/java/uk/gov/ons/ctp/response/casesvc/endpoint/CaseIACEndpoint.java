package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.net.URI;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.service.CaseIACService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

@RestController
@RequestMapping(value = "/cases/{caseId}/iac", produces = "application/json")
@Slf4j
public final class CaseIACEndpoint implements CTPEndpoint {

  private CaseService caseService;
  private CaseIACService caseIACService;

  @Autowired
  public CaseIACEndpoint(CaseService caseService, CaseIACService caseIACService) {
    this.caseService = caseService;
    this.caseIACService = caseIACService;
  }

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<CaseIACDTO> generateIACCode(@PathVariable("caseId") final UUID caseId) {

    Case actualCase = caseService.findCaseById(caseId);

    if (actualCase == null) {
      return ResponseEntity.notFound().build();
    }

    String iac = caseIACService.generateNewCaseIACCode(actualCase.getCasePK());
    CaseIACDTO dto = new CaseIACDTO(iac);

    URI uri = ServletUriComponentsBuilder.fromCurrentRequest().build().toUri();

    return ResponseEntity.created(uri).body(dto);
  }

  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<CaseIACDTO>> getIACCodes(@PathVariable("caseId") final UUID caseId) {

    Case actualCase = caseService.findCaseById(caseId);

    if (actualCase == null) {
      return ResponseEntity.notFound().build();
    }

    List<CaseIACDTO> iacs =
        actualCase
            .getIacAudits()
            .stream()
            .map(CaseIacAudit::getIac)
            .map(CaseIACDTO::new)
            .collect(Collectors.toList());

    return ResponseEntity.ok(iacs);
  }

  @Data
  @AllArgsConstructor
  protected static class CaseIACDTO {
    private String iac;
  }
}
