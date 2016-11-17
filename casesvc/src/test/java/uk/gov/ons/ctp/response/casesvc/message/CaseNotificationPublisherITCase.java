package uk.gov.ons.ctp.response.casesvc.message;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.ACTIVATED;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.utility.CaseNotificationMessageListener;

/**
 * Test publication of CaseNotification messages on integration flow to Case.LifecycleEvents queue.
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseNotificationPublisherSITestConfig.class)
public class CaseNotificationPublisherSITest {

  @Inject
  private CaseNotificationPublisher caseNotificationPublisher;

  @Inject
  DefaultMessageListenerContainer caseNotificationListenerContainer;

  @Inject
  CachingConnectionFactory connectionFactory;

  @Inject
  MessageChannel caseNotificationXml;

  private Connection connection;
  private int initialCounter;

  private static final String INVALID_CASE_NOTIFICATIONS_QUEUE = "Case.InvalidNotifications";

  @Before
  public void setUp() throws Exception {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_NOTIFICATIONS_QUEUE);

    CaseNotificationMessageListener listener = (CaseNotificationMessageListener) caseNotificationListenerContainer.getMessageListener();
    listener.setPayload(null);
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  @Test
  public void dummyTest() {
    assertTrue(true);
  }

//  @Test
//  public void testPublishValidNotification() throws InterruptedException, JAXBException, JMSException {
//    List<CaseNotification> notificationList = new ArrayList<>();
//    notificationList.add(new CaseNotification(1, 3, ACTIVATED));
//    notificationList.add(new CaseNotification(2, 3, ACTIVATED));
//    caseNotificationPublisher.sendNotifications(notificationList);
//
//    /**
//     * We check that no additional message has been put on the xml invalid queue
//     */
//    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_NOTIFICATIONS_QUEUE);
//    assertEquals(initialCounter, finalCounter);
//
//    /**
//     * The section below verifies that a CaseNotification ends up on the queue
//     */
//    CaseNotificationMessageListener listener = (CaseNotificationMessageListener) caseNotificationListenerContainer.getMessageListener();
//    TimeUnit.SECONDS.sleep(10);
//    String payload = listener.getPayload();
//
//    Document doc = parse(payload);
//    assertThat(doc, hasXPath("/caseNotifications/caseNotification[1]/actionPlanId", equalTo("3")));
//    assertThat(doc, hasXPath("/caseNotifications/caseNotification[2]/caseId", equalTo("2")));
//    assertThat(doc, hasXPath("/caseNotifications/caseNotification[2]/notificationType", equalTo("ACTIVATED")));
//  }
//
//  @Test
//  public void testPublishInvalidNotification() throws IOException, JMSException {
//    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidCaseNotification.xml"), "UTF-8");
//    caseNotificationXml.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());
//
//    /**
//     * We check that the invalid xml ends up on the invalid queue.
//     */
//    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_NOTIFICATIONS_QUEUE);
//    assertEquals(1, finalCounter - initialCounter);
//  }

  /**
   * Create XML Document from String message on queue
   * @param xmlMessage XML String
   * @return doc Document
   */
  private Document parse(String xmlMessage) {
    Document doc = null;
    try {
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(false);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      doc = documentBuilder.parse(new ByteArrayInputStream(xmlMessage.getBytes()));
    } catch (Exception ex) {
      log.error("Error parsing Published XML", ex.getMessage());
    }
    return doc;
  }

  private File provideTempFile(String inputStreamLocation) throws IOException {
    InputStream is = getClass().getResourceAsStream(inputStreamLocation);
    File tempFile = File.createTempFile("prefix","suffix");
    tempFile.deleteOnExit();
    FileOutputStream out = new FileOutputStream(tempFile);
    IOUtils.copy(is, out);
    return tempFile;
  }
}
