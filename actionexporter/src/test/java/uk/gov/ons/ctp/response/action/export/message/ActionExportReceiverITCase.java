package uk.gov.ons.ctp.response.action.export.message;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;
import javax.jms.Connection;
import javax.jms.JMSException;

import lombok.extern.slf4j.Slf4j;
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
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.message.JmsHelper;
import uk.gov.ons.ctp.response.action.export.service.ActionExportService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Test focusing on Spring Integration
 */
@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ActionExportReceiverITCaseConfig.class)
public class ActionExportReceiverITCase {

  @Inject
  private QueueChannel activeMQDLQXml;

  @Inject
  private MessageChannel testOutbound;

  @Inject
  private MessageChannel instructionXml;

  @Inject
  private ActionExportService actionExportService;

  @Inject
  @Qualifier("instructionUnmarshaller")
  private Jaxb2Marshaller instructionUnmarshaller;

  @Inject
  CachingConnectionFactory connectionFactory;

  private Connection connection;
  private int initialCounter;

  private static final int RECEIVE_TIMEOUT = 20000;
  private static final String IAC_1 = "12345678930666392556";
  private static final String IAC_2 = "12345678930666392557";
  private static final String INVALID_ACTION_INSTRUCTION_QUEUE = "Action.InvalidActionInstructions";
  private static final String PACKAGE_ACTION_INSTRUCTION = "uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction";


  @Before
  public void setUp() throws Exception {
    Date now = new Date();
    long startSetUp = now.getTime();
    connection = connectionFactory.createConnection();
    connection.start();
    initialCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTION_QUEUE);

    String jaxbContext = instructionUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_ACTION_INSTRUCTION));

    reset(actionExportService);

    activeMQDLQXml.clear();

    now = new Date();
    long endSetUp = now.getTime();
    log.debug("time takenBySetup: {}", endSetUp - startSetUp);
  }

  @After
  public void finishCleanly() throws JMSException {
    Date now = new Date();
    long startFinishCleanly = now.getTime();

    connection.close();

    now = new Date();
    long endFinishCleanly = now.getTime();
    log.debug("time takenByFinishCleanly: {}", endFinishCleanly - startFinishCleanly);
  }

  /**
   * SI sends badly formed XML to generate a parse error. It results in an  ActiveMQ dead letter queue message. Local
   * transaction should rollback and message should be considered a poisoned bill.
   */
  @Test
  public void testInstructionXmlBadlyFormed() throws IOException, JMSException {
    Date now = new Date();
    long startBadlyFormed = now.getTime();

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/badlyFormedActionInstruction.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    assertEquals(testMessage, payload);

    /**
     * We check that no badly formed xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTION_QUEUE);
    assertEquals(0, finalCounter - initialCounter);

    now = new Date();
    long endBadlyFormed = now.getTime();
    log.debug("time takenByBadlyFormed: {}", endBadlyFormed - startBadlyFormed);
  }

  /**
   * Test invalid well formed XML should go to the invalid queue
   */
  @Test
  public void testInstructionXmlInvalid() throws InterruptedException, IOException, JMSException {
    Date now = new Date();
    long startXmlInvalid = now.getTime();

    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/invalidActionInstruction.xml"), "UTF-8");

    instructionXml.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    /**
     * We check that the invalid xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTION_QUEUE);
    assertEquals(1, finalCounter - initialCounter);

    now = new Date();
    long endXmlInvalid = now.getTime();
    log.debug("time takenByXmlInvalid: {}", endXmlInvalid - startXmlInvalid);
  }

  /**
   * Test whole SI flow with valid XML
   *
   * @throws InterruptedException if CountDownLatch interrupted
   */
  @Test
  public void testInstructionXmlValid() throws InterruptedException, IOException, JMSException {
    Date now = new Date();
    long startXmlValid = now.getTime();

    // SetUp CountDownLatch for synchronisation with async call
    final CountDownLatch serviceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock actionExportService.acceptInstruction method is called
    doAnswer(countsDownLatch(serviceInvoked)).when(actionExportService).acceptInstruction(any());

    // Send message
    String testMessage = FileUtils.readFileToString(provideTempFile("/xmlSampleFiles/validActionInstruction.xml"), "UTF-8");
    testOutbound.send(org.springframework.messaging.support.MessageBuilder.withPayload(testMessage).build());

    // Await synchronisation with the asynchronous message call
    serviceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    /**
     * We check that no xml ends up on the invalid queue.
     */
    int finalCounter = JmsHelper.numberOfMessagesOnQueue(connection, INVALID_ACTION_INSTRUCTION_QUEUE);
    assertEquals(0, finalCounter - initialCounter);

    ArgumentCaptor<ActionInstruction> argumentCaptor = ArgumentCaptor.forClass(ActionInstruction.class);
    verify(actionExportService).acceptInstruction(argumentCaptor.capture());
    assertEquals(argumentCaptor.getValue().getActionRequests().getActionRequests().get(0).getIac(), IAC_1);
    assertEquals(argumentCaptor.getValue().getActionRequests().getActionRequests().get(1).getIac(), IAC_2);

    now = new Date();
    long endXmlValid = now.getTime();
    log.debug("time takenByXmlValid: {}", endXmlValid - startXmlValid);
  }

  /**
   * Should be called when mock method is called in asynchronous test to
   * countDown the CountDownLatch test thread is waiting on.
   *
   * @param serviceInvoked CountDownLatch to countDown
   * @return Answer<CountDownLatch> Mockito Answer object
   */
  private Answer<CountDownLatch> countsDownLatch(final CountDownLatch serviceInvoked) {
    return new Answer<CountDownLatch>() {
      @Override
      public CountDownLatch answer(InvocationOnMock invocationOnMock)
          throws Throwable {
        serviceInvoked.countDown();
        return null;
      }
    };
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
