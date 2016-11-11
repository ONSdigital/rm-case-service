package uk.gov.ons.ctp.response.casesvc.utility;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.activemq.command.ActiveMQTextMessage;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;

@Slf4j
@Data
public class CaseNotificationMessageListener implements MessageListener {

  private String payload;

  @Override
  public void onMessage(Message arg0) {
    log.debug("onMessage entrance with {}", arg0);
    try {
      ActiveMQTextMessage theMsg = (ActiveMQTextMessage)arg0;
      payload = theMsg.getText();
      log.debug("payload = {}", payload);
    } catch (JMSException e) {
      log.error("error retrieving message - ", e.getMessage());
    }

  }

}

