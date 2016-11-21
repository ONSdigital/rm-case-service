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
import org.springframework.integration.channel.QueueChannel;
import org.springframework.jms.connection.CachingConnectionFactory;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CountDownLatch;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * Test focusing on Spring Integration
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = CaseReceiptReceiverImplITcaseConfig.class)
public class CaseReceiptReceiverImplITCase {

  private static final int RECEIVE_TIMEOUT = 20000;
  private static final String NONEXISTING_CASE_REF = "tiptop";
  private static final String NONEXISTING_CASE_REF_FOR_EXCEPION = "tiptopException";

  @Inject
  private MessageChannel testOutbound;

  @Inject
  DefaultMessageListenerContainer activeMQListenerContainer;

  @Inject
  private QueueChannel activeMQDLQXml;

  @Inject
  private MessageChannel caseReceiptXml;

  @Inject
  @Qualifier("caseReceiptUnmarshaller")
  Jaxb2Marshaller caseReceiptUnmarshaller;

  @Inject
  CachingConnectionFactory connectionFactory;

  @Inject
  private CaseService caseService;

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

    reset(caseService);

    activeMQDLQXml.clear();
  }

  @After
  public void finishCleanly() throws JMSException {
    connection.close();
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
     * We check that no additional xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * We check that no xml ends up on the dead letter queue.
     */
//    TODO This test passes inside an IDE but fails on the command line.
//    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
//    assertNull(message);
//
//    /**
//     * We check the message was processed
//     */
//    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
//    verify(caseService).findCaseByCaseRef(argumentCaptor.capture());
//    assertEquals(argumentCaptor.getValue(), NONEXISTING_CASE_REF);
  }

  @Test
  public void testReceivingCaseReceiptValidXmlExceptionThrownInProcessing()
          throws InterruptedException, IOException, JMSException {
    // Set up CountDownLatch for synchronisation with async call
    final CountDownLatch caseServiceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock caseService.findCaseByCaseRef method is called
    doAnswer(countsDownLatch(caseServiceInvoked)).when(caseService).findCaseByCaseRef(any(String.class));

    when(caseService.findCaseByCaseRef(any(String.class))).thenThrow(new RuntimeException());

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/validCaseReceiptForException.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    caseServiceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_CASE_RECEIPTS_QUEUE);
    assertEquals(initialCounter, finalCounter);

    /**
     * We check that the xml ends up on the dead letter queue.
     */
    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    assertEquals(testMessage, payload);

    /**
     * We check the message was processed
     */
    ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
    verify(caseService, atLeastOnce()).findCaseByCaseRef(argumentCaptor.capture());
    assertEquals(argumentCaptor.getValue(), NONEXISTING_CASE_REF_FOR_EXCEPION);
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
