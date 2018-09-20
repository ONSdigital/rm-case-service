package uk.gov.ons.ctp.response.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.time.Clock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.client.InternetAccessCodeSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseIacAuditRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

@Service
public class CaseIACService {

  private static final Logger log = LoggerFactory.getLogger(CaseIACService.class);

  private final InternetAccessCodeSvcClient iacClient;
  private final CaseIacAuditRepository caseIacAuditRepo;
  private final CaseEventRepository caseEventRepo;
  private final Clock clock;

  @Autowired
  public CaseIACService(
      final InternetAccessCodeSvcClient iacClient,
      final CaseIacAuditRepository caseIacAuditRepo,
      final CaseEventRepository caseEventRepo,
      final Clock clock) {
    this.iacClient = iacClient;
    this.caseIacAuditRepo = caseIacAuditRepo;
    this.caseEventRepo = caseEventRepo;
    this.clock = clock;
  }

  public String findCaseIacByCasePK(int caseFK) {
    log.debug("Entering findCaseIacByCasePK");

    CaseIacAudit caseIacAudit = caseIacAuditRepo.findTop1ByCaseFKOrderByCreatedDateTimeDesc(caseFK);

    if (caseIacAudit == null) {
      return null;
    }

    return caseIacAudit.getIac();
  }

  public CaseIacAudit findCaseByIac(String iac) throws CTPException {
    log.debug("Entering findCaseByIac");

    CaseIacAudit caseIacAudit = caseIacAuditRepo.findByIac(iac);

    if (caseIacAudit == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND, String.format("Cannot find Case for Iac %s", iac));
    }

    return caseIacAudit;
  }

  @Transactional
  public String generateNewCaseIACCode(final Integer casePk) {
    final String iac = iacClient.generateIACs(1).get(0);
    saveNewIacForCase(casePk, iac);
    auditCaseEvent(casePk);
    return iac;
  }

  private void auditCaseEvent(Integer casePk) {
    CaseEvent caseEvent =
        CaseEvent.builder()
            .category(CategoryDTO.CategoryName.GENERATE_ENROLMENT_CODE)
            .caseFK(casePk)
            .createdDateTime(Timestamp.from(clock.instant()))
            .build();
    caseEventRepo.save(caseEvent);
  }

  private void saveNewIacForCase(final Integer casePk, final String iac) {
    CaseIacAudit audit =
        CaseIacAudit.builder()
            .caseFK(casePk)
            .createdDateTime(Timestamp.from(clock.instant()))
            .iac(iac)
            .build();
    caseIacAuditRepo.save(audit);
  }

  public void disableAllIACsForCase(Case caze) {
    caze.getIacAudits()
        .forEach(
            caseIacAudit -> {
              if (!iacClient.disableIAC(caseIacAudit.getIac())) {
                log.with("caseId", caze.getId()).error("Failed to disable an IAC for case");
              }
            });
  }
}
