package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandlingException;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptProcessErrorReceiver;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

@Slf4j
@MessageEndpoint
public class CaseReceiptProcessErrorReceiverImpl implements CaseReceiptProcessErrorReceiver {

  @ServiceActivator(inputChannel = "caseReceiptProcessError")
  public void process(MessageHandlingException exception) {
    log.debug("entering process with exception {}", exception);
    CaseReceipt caseReceiptToReprocess = (CaseReceipt)exception.getFailedMessage().getPayload();
    log.debug("caseReceiptToReprocess = {}", caseReceiptToReprocess);
    // TODO Republish to queue Case.Responses
  }

}
