package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptProcessErrorReceiver;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

import javax.inject.Inject;

/**
 * The reader of messages put on channel caseReceiptProcessErrorFailedMsgOnly
 */
@Slf4j
@MessageEndpoint
public class CaseReceiptProcessErrorReceiverImpl implements CaseReceiptProcessErrorReceiver {

  @Inject
  private CaseReceiptPublisher caseReceiptPublisher;

  /**
   * To process messages put on channel caseReceiptProcessErrorFailedMsgOnly
   * @param message the message to process
   */
  @ServiceActivator(inputChannel = "caseReceiptProcessErrorFailedMsgOnly")
  public void process(Message<?> message) {
    log.debug("entering process with message {}", message);
    CaseReceipt caseReceiptToReprocess = (CaseReceipt)message.getPayload();
    log.debug("caseReceiptToReprocess = {}", caseReceiptToReprocess);

    caseReceiptPublisher.send(caseReceiptToReprocess);
  }

}
