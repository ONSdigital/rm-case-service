package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandlingException;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptProcessErrorReceiver;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

import javax.inject.Inject;

/**
 * The reader of messages put on channel caseReceiptProcessError
 */
@Slf4j
@MessageEndpoint
public class CaseReceiptProcessErrorReceiverImpl implements CaseReceiptProcessErrorReceiver {

  @Inject
  private CaseReceiptPublisher caseReceiptPublisher;

  /**
   * To process exceptions put on channel caseReceiptProcessError
   * @param exception the exception to process
   */
  @ServiceActivator(inputChannel = "caseReceiptProcessError")
  public void process(MessageHandlingException exception) {
    log.debug("entering process with exception {}", exception);
    CaseReceipt caseReceiptToReprocess = (CaseReceipt)exception.getFailedMessage().getPayload();
    log.debug("caseReceiptToReprocess = {}", caseReceiptToReprocess);

    caseReceiptPublisher.send(caseReceiptToReprocess);
  }

}
