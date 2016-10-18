package uk.gov.ons.ctp.response.casesvc.message.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.MessageHandlingException;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptProcessErrorReceiver;
import uk.gov.ons.ctp.response.casesvc.message.CaseReceiptPublisher;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

import javax.inject.Inject;

@Slf4j
@MessageEndpoint
public class CaseReceiptProcessErrorReceiverImpl implements CaseReceiptProcessErrorReceiver {

  @Inject
  private CaseReceiptPublisher caseReceiptPublisher;

  // TODO we want this to wake up occasionally only - use a Poller?
  @ServiceActivator(inputChannel = "caseReceiptProcessError")
  public void process(MessageHandlingException exception) {
    log.debug("entering process with exception {}", exception);
    CaseReceipt caseReceiptToReprocess = (CaseReceipt)exception.getFailedMessage().getPayload();
    log.debug("caseReceiptToReprocess = {}", caseReceiptToReprocess);

    caseReceiptPublisher.send(caseReceiptToReprocess);
  }

}
