package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseIacAudit;

/**
 * The Case Iac Audit Service interface defines all business behaviours for operations on the Case
 * audit entities model.
 */
public interface CaseIACService extends CTPService {

  String generateNewCaseIACCode(final Integer casePk);

  String findCaseIacByCasePK(int i) throws CTPException;

  CaseIacAudit findCaseByIac(String iac) throws CTPException;
}
