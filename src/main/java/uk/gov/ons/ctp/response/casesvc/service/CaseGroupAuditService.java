package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;

public interface CaseGroupAuditService {

    /**
     * Updates the audit table for any alterations to the casegroupstatus for any casegroup
     * @param caseGroup
     * @param caseEvent
     */
    void updateAuditTable(final CaseGroup caseGroup, final CaseEvent caseEvent) ;

}