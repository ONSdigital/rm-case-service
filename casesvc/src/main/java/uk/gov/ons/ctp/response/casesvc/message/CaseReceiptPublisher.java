package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

/**
 * The publisher to queues
 */
public interface CaseReceiptPublisher {
  /**
   * To publish a caseReceipt to queue
   * @param caseReceipt to be published
   * @return the published caseFeedback
   */
  CaseReceipt send(CaseReceipt caseReceipt);
}
