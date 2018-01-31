package uk.gov.ons.ctp.response.casesvc.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroupStatusAudit;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupAuditService;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupStatusAuditRepository;

@Service
@Slf4j
public class CaseGroupAuditServiceImpl implements CaseGroupAuditService {

    @Autowired
    private CaseGroupStatusAuditRepository caseGroupStatusAuditRepository;

    @Override
    public void updateAuditTable(final CaseGroup caseGroup, final CaseEvent caseEvent) {
        CaseGroupStatusAudit auditEntity = new CaseGroupStatusAudit();

        auditEntity.setCaseGroupFK(caseGroup.getCaseGroupPK());
        auditEntity.setStatus(caseGroup.getStatus());
        auditEntity.setCreatedDateTime(DateTimeUtil.nowUTC());
        //TODO: DON'T ACTUALLY HAVE THE PARTY ID? OR IS THIS IT IN STRING
        auditEntity.setCreatedBy(caseEvent.getCreatedBy());
        caseGroupStatusAuditRepository.saveAndFlush(auditEntity);

    }
}