package uk.gov.ons.ctp.response.casesvc.service;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;

public interface UnlinkedCaseReceiptService extends CTPService {
  UnlinkedCaseReceipt createUnlinkedCaseReceipt(UnlinkedCaseReceipt unlinkedCaseReceipt);
}
