package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;

/**
 * The service to deal with unlinkedCaseReceipts.
 */
public interface UnlinkedCaseReceiptService extends CTPService {
  /**
   * To store an unlinkedCaseReceipt
   * @param unlinkedCaseReceipt to be stored
   * @return the stored unlinkedCaseReceipt
   */
  UnlinkedCaseReceipt createUnlinkedCaseReceipt(UnlinkedCaseReceipt unlinkedCaseReceipt);
}
