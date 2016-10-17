package uk.gov.ons.ctp.response.casesvc.utility;

import javax.jms.*;
import java.util.Enumeration;

/**
 * Utility class for anything to do with JMS in our tests
 */
public class JmsHelper {

  /**
   * To count the number of messages on the given queue
   * @param connection
   * @param queueName
   * @return the number of messages on the given queue
   * @throws JMSException
   */
  public static int numberOfMessagesOnQueue(Connection connection, String queueName) throws JMSException {
    Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
    Queue queue = session.createQueue(queueName);
    QueueBrowser browser = session.createBrowser(queue);
    Enumeration msgs = browser.getEnumeration();
    int counter = 0;
    while (msgs.hasMoreElements()) {
      msgs.nextElement();
      counter = counter + 1;
    }

    browser.close();
    session.close();

    return counter;
  }
}
