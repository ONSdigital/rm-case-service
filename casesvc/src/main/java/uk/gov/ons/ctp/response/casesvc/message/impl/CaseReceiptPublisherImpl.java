package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Publisher;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

import javax.inject.Named;

/**
 * The publisher to queues
 */
@Slf4j
@Named
public class CaseReceiptPublisherImpl implements CaseReceiptPublisher {
  @Publisher(channel = "caseReceiptOutbound")
  @Override
  public CaseReceipt send(CaseReceipt caseReceipt) {
    log.debug("send to queue caseReceipt {}", caseReceipt);
    return caseReceipt;
  }
}