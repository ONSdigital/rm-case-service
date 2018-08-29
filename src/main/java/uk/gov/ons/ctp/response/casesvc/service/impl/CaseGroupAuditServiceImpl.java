package uk.gov.ons.ctp.response.casesvc.service.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroupStatusAudit;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupStatusAuditRepository;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupAuditService;

@Service
public class CaseGroupAuditServiceImpl implements CaseGroupAuditService {
  private static final Logger log = LoggerFactory.getLogger(CaseGroupAuditServiceImpl.class);

  @Autowired private CaseGroupStatusAuditRepository caseGroupStatusAuditRepository;

  @Override
  public void updateAuditTable(final CaseGroup caseGroup, final UUID partyId) {
    CaseGroupStatusAudit auditEntity = new CaseGroupStatusAudit();

    auditEntity.setCaseGroupFK(caseGroup.getCaseGroupPK());
    auditEntity.setStatus(caseGroup.getStatus());
    auditEntity.setPartyId(partyId);
    auditEntity.setCreatedDateTime(DateTimeUtil.nowUTC());
    log.with("audit_entity", auditEntity).debug("Updating the caseGroupStatus");
    caseGroupStatusAuditRepository.saveAndFlush(auditEntity);
  }
}
