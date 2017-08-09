package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

/**
 * The reader of CaseReceipts from queue
 */
public interface CaseReceiptReceiver {
  /**
   * To process CaseReceipts read from queue
   * @param caseReceipt to process
   * @throws CTPException when case state transition error
   */
  void process(CaseReceipt caseReceipt) throws CTPException;
}
