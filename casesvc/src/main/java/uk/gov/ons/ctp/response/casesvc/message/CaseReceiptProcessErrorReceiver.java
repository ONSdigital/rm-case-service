package uk.gov.ons.ctp.response.casesvc.message;

import org.springframework.messaging.MessageHandlingException;

/**
 * This is the receiver for messages put on channel caseReceiptProcessError
 */
public interface CaseReceiptProcessErrorReceiver {
  /**
   * To process exceptions put on channel caseReceiptProcessError
   * @param exception the exception to process
   */
  void process(MessageHandlingException exception);
}
