package uk.gov.ons.ctp.response.casesvc.message;

import org.springframework.messaging.Message;

/**
 * The reader of messages put on channel caseReceiptProcessErrorFailedMsgOnly
 */
public interface CaseReceiptProcessErrorReceiver {
  /**
   * To process messages put on channel caseReceiptProcessErrorFailedMsgOnly
   * @param message the message to process
   */
  void process(Message<?> message) ;
}