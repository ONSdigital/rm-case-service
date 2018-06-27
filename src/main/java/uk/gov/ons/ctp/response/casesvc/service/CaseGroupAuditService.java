package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;

import java.util.UUID;

public interface CaseGroupAuditService {

  /** Updates the audit table for any alterations to the casegroupstatus for any casegroup */
  void updateAuditTable(final CaseGroup caseGroup, final UUID partyId);
}
