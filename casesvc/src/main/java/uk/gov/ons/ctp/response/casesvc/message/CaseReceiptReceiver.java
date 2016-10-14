package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

public interface CaseReceiptReceiver {
  void process(CaseReceipt caseReceipt);
}
