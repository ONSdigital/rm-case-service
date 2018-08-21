package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.sql.Timestamp;
import java.time.Clock;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseIacAuditRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseIACService;
import uk.gov.ons.ctp.response.casesvc.service.InternetAccessCodeSvcClientService;

@Service
@Slf4j
public class CaseIACServiceImpl implements CaseIACService {

  private final InternetAccessCodeSvcClientService iacClient;
  private final CaseIacAuditRepository caseIacAuditRepo;
  private final CaseEventRepository caseEventRepo;
  private final Clock clock;

  @Autowired
  public CaseIACServiceImpl(
      final InternetAccessCodeSvcClientService iacClient,
      final CaseIacAuditRepository caseIacAuditRepo,
      final CaseEventRepository caseEventRepo,
      final Clock clock) {
    this.iacClient = iacClient;
    this.caseIacAuditRepo = caseIacAuditRepo;
    this.caseEventRepo = caseEventRepo;
    this.clock = clock;
  }

  @Override
  public String findCaseIacByCasePK(int caseFK) throws CTPException {
    log.debug("Entering findCaseIacByCasePK");

    CaseIacAudit caseIacAudit = caseIacAuditRepo.findTop1ByCaseFKOrderByCreatedDateTimeDesc(caseFK);

    if (caseIacAudit == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("Cannot find Iac for case %s", caseFK));
    }

    return caseIacAudit.getIac();
  }

  @Override
  public CaseIacAudit findCaseByIac(String iac) throws CTPException {
    log.debug("Entering findCaseByIac");

    CaseIacAudit caseIacAudit = caseIacAuditRepo.findByIac(iac);

    if (caseIacAudit == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("Cannot find Case for Iac %s", iac));
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
            .createdBy(null) // TODO
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
}
