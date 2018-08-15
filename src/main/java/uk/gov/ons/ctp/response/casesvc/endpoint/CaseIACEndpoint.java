package uk.gov.ons.ctp.response.casesvc.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping(value = "/cases/{caseId}/iac", produces = "application/json")
@Slf4j
public final class CaseIACEndpoint implements CTPEndpoint {

  @Autowired
  private CaseService caseService;

  @RequestMapping(method = RequestMethod.POST)
  public ResponseEntity<String> generateIACCode(
      @PathVariable("caseId") final UUID caseId) {

    Case actualCase = caseService.findCaseById(caseId);

    if (actualCase == null) {
      return ResponseEntity.notFound().build();
    }

    String iac = caseService.generateNewCaseIACCode(caseId);

    return ResponseEntity.created(URI.create("")).body(iac);
  }
}
