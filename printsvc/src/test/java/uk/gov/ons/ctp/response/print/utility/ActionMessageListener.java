package uk.gov.ons.ctp.response.print.utility;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTextMessage;

/**
 * What appears to be a dummy JMS listener for a spring integration test
 */
@Slf4j
@Data
public class ActionMessageListener implements MessageListener {

  private String payload;

  /**
   * handle a message
   */
  @Override
  public void onMessage(Message arg0) {
    log.debug("onMessage entrance with {}", arg0);
    try {
      ActiveMQTextMessage theMsg = (ActiveMQTextMessage) arg0;
      payload = theMsg.getText();
      log.debug("payload = {}", payload);
    } catch (JMSException e) {
      log.error("error retrieving message - ", e.getMessage());
    }
  }
}
