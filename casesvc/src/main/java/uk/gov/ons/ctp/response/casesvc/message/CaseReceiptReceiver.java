package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

/**
 * The reader of CaseReceipts from queue
 */
public interface CaseReceiptReceiver {
  /**
   * To process CaseReceipts read from queue
   * @param caseReceipt to process
   */
  void process(CaseReceipt caseReceipt);
}
