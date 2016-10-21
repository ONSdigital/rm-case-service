package uk.gov.ons.ctp.response.casesvc.message;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasXPath;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType.ACTIVATED;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.messaging.Message;
import org.springframework.messaging.PollableChannel;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.w3c.dom.Document;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/**
 * Test publication of CaseNotification messages on integration flow to
 * Case.Notifications queue.
 *
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseNotificationPublisherTestConfig.class)
public class CaseNotificationPublisherTest {

  private static final int RECEIVE_TIMEOUT = 5000;

  @Inject
  private PollableChannel notificationXml;

  @Inject
  private CaseNotificationPublisher notificationPublisher;

  /**
   * Test Publication of Case creation events.
   */
  @Test
  public void testNotificationPublisher() {
    assertTrue(true);
    List<CaseNotification> notificationList = new ArrayList<>();
    notificationList.add(new CaseNotification(1, 3, ACTIVATED));
    notificationList.add(new CaseNotification(2, 3, ACTIVATED));
    notificationPublisher.sendNotifications(notificationList);
    Message<?> message = notificationXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    Document doc = parse(payload);
    assertThat(doc, hasXPath("/caseNotifications/caseNotification[1]/actionPlanId", equalTo("3")));
    assertThat(doc, hasXPath("/caseNotifications/caseNotification[2]/caseId", equalTo("2")));
    assertThat(doc, hasXPath("/caseNotifications/caseNotification[2]/notificationType", equalTo("ACTIVATED")));
  }

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
}
