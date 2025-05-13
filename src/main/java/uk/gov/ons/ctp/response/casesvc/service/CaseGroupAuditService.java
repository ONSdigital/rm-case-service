package uk.gov.ons.ctp.response.casesvc.service;

import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroupStatusAudit;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupStatusAuditRepository;

@Service
public class CaseGroupAuditService {
  private static final Logger log = LoggerFactory.getLogger(CaseGroupAuditService.class);

  @Autowired private CaseGroupStatusAuditRepository caseGroupStatusAuditRepository;

  /** Updates the audit table for any alterations to the casegroupstatus for any casegroup */
  public void updateAuditTable(final CaseGroup caseGroup, final UUID partyId) {
    CaseGroupStatusAudit auditEntity = new CaseGroupStatusAudit();

    auditEntity.setCaseGroupFK(caseGroup.getCaseGroupPK());
    auditEntity.setStatus(caseGroup.getStatus());
    auditEntity.setPartyId(partyId);
    auditEntity.setCreatedDateTime(caseGroup.getStatusChangeTimestamp());
    log.with("audit_entity", auditEntity).debug("Updating the caseGroupStatus");
    caseGroupStatusAuditRepository.saveAndFlush(auditEntity);
  }
}
