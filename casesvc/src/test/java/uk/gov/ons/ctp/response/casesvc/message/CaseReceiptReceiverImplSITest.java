package uk.gov.ons.ctp.response.casesvc.message;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.UnexpectedRollbackException;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.service.impl.CaseServiceImpl;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Test focusing on Spring Integration
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseReceiptReceiverImplSITestConfig.class)
public class CaseReceiptReceiverImplSITest {

  private static final int RECEIVE_TIMEOUT = 20000;

  @Inject
  private MessageChannel testOutbound;

  @Inject
  private PollableChannel activeMQDLQXml;

  @Inject
  private MessageChannel caseReceiptXml;

  @Inject
  @Qualifier("caseReceiptUnmarshaller")
  Jaxb2Marshaller caseReceiptUnmarshaller;

  @Inject
  CachingConnectionFactory connectionFactory;

  @Inject
  private CaseServiceImpl caseService;

  @Inject
  private CaseReceiptPublisher caseReceiptPublisher;

  private Connection connection;
  private int initialCounter;

  private static final String INVALID_CASE_RECEIPTS_QUEUE = "Case.InvalidCaseReceipts";
  private static final String PACKAGE_CASE_RECEIPT
          = "uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt";

  @Before
  public void setUp() throws Exception {
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);

    String jaxbContext = caseReceiptUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_CASE_RECEIPT));
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
  }

  @Test
  public void testReceivingCaseReceiptXmlBadlyFormed() throws IOException, JMSException {
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/badlyFormedCaseReceipt.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the badly formed xml ends up on the dead letter queue.
     */
    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    assertEquals(testMessage, payload);

    /**
     * We check that no badly formed xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(0, finalCounter - initialCounter);

  }

  @Test
  public void testReceivingCaseReceiptInvalidXml() throws IOException, JMSException {
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidCaseReceipt.xml"), "UTF-8");

    caseReceiptXml.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the invalid xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(1, finalCounter - initialCounter);
  }

  @Test
  public void testReceivingCaseReceiptValidXml() throws InterruptedException, IOException, JMSException {
    // Set up CountDownLatch for synchronisation with async call
    final CountDownLatch caseServiceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock caseService.findCaseByCaseRef method is called
    doAnswer(countsDownLatch(caseServiceInvoked)).when(caseService).findCaseByCaseRef(any(String.class));

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/validCaseReceipt.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    caseServiceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * We check that no xml ends up on the dead letter queue.
     */
    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    assertNull(message);

    /**
     * We check the message was processed
     */
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(caseService).findCaseByCaseRef(argumentCaptor.capture());
    assertEquals(argumentCaptor.getValue(), "tiptop");
  }

  @Test
  public void testReceivingCaseReceiptValidXmlExceptionThrownInProcessing()
          throws InterruptedException, IOException, JMSException {
    // Set up CountDownLatch for synchronisation with async call
    final CountDownLatch caseServiceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock caseService.findCaseByCaseRef method is called
    doAnswer(countsDownLatch(caseServiceInvoked)).when(caseService).findCaseByCaseRef(any(String.class));

    when(caseService.findCaseByCaseRef(any(String.class))).thenThrow(new RuntimeException());

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/validCaseReceipt.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    caseServiceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * We check that no xml ends up on the dead letter queue.
     */
    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    assertNull(message);

    /**
     * We check the message was processed by CaseReceiptReceiverImpl
     */
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(caseService).findCaseByCaseRef(argumentCaptor.capture());
    assertEquals(argumentCaptor.getValue(), "tiptop");

    /**
     * We check that the message was processed by CaseReceiptProcessErrorReceiverImpl
     */
    verify(caseReceiptPublisher).send(any(CaseReceipt.class));
  }

  private File provideTempFile(String inputStreamLocation) throws IOException {
    InputStream is = getClass().getResourceAsStream(inputStreamLocation);
    File tempFile = File.createTempFile("prefix","suffix");
    tempFile.deleteOnExit();
    FileOutputStream out = new FileOutputStream(tempFile);
    IOUtils.copy(is, out);
    return tempFile;
  }

  /**
   * Should be called when mock method is called in asynchronous test to countDown the CountDownLatch test thread is
   * waiting on.
   *
   * @param serviceInvoked CountDownLatch to countDown
   * @return Answer<CountDownLatch> Mockito Answer object
   */
  private Answer<CountDownLatch> countsDownLatch(final CountDownLatch serviceInvoked) {
    return new Answer<CountDownLatch>() {
      @Override
      public CountDownLatch answer(InvocationOnMock invocationOnMock) throws Throwable {
        serviceInvoked.countDown();
        return null;
      }
    };
  }
}
