package uk.gov.ons.ctp.response.casesvc.service.impl;

import javax.inject.Inject;
import javax.inject.Named;

import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;
import uk.gov.ons.ctp.response.casesvc.domain.repository.UnlinkedCaseReceiptRepository;
import uk.gov.ons.ctp.response.casesvc.service.UnlinkedCaseReceiptService;

/**
 * The service to deal with unlinkedCaseReceipts.
 */
@Named
public class UnlinkedCaseReceiptServiceImpl implements UnlinkedCaseReceiptService {

  @Inject
  private UnlinkedCaseReceiptRepository unlinkedCaseReceiptRepository;

  /**
   * To store an unlinkedCaseReceipt
   * @param unlinkedCaseReceipt to be stored
   * @return the stored unlinkedCaseReceipt
   */
  @Override
  public UnlinkedCaseReceipt createUnlinkedCaseReceipt(UnlinkedCaseReceipt unlinkedCaseReceipt) {
    return unlinkedCaseReceiptRepository.saveAndFlush(unlinkedCaseReceipt);
  }
}
