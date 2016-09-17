package uk.gov.ons.ctp.response.action.export.message;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import uk.gov.ons.ctp.response.action.export.service.impl.ActionExportServiceImpl;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Test Spring Integration flow of Case Notification life cycle messages
 *
 */
@ContextConfiguration(locations = {"/PrintServiceTest-context.xml"})
@TestPropertySource("classpath:/application-test.properties")
@RunWith(SpringJUnit4ClassRunner.class)
public class ActionExportReceiverTest {

  private static final int RECEIVE_TIMEOUT = 20000;
  private static final String INVALID_ACTION_INSTRUCTION_LOG_DIRECTORY = "/tmp/ctp/logs/actionexporter/instruction";
  private static final String VALIDXML_PART1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
      + "<p:actionInstruction xmlns:p=\"http://ons.gov.uk/ctp/response/action/message/instruction\" "
      + "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
      + "xsi:schemaLocation=\"http://ons.gov.uk/ctp/response/action/message/instruction actionInstruction.xsd \">"
      + "<actionRequests>"
      + "<actionRequest>"
      + "<responseRequired>false</responseRequired>"
      + "<actionId>1</actionId>"
      + "<actionPlan>actionPlan</actionPlan>"
      + "<actionType>actionType</actionType>"
      + "<address>"
      + "<uprn>1</uprn>"
      + "<type>type</type>"
      + "<estabType>estabType</estabType>"
      + "<locality>locality</locality>"
      + "<organisationName>organisationName</organisationName>"
      + "<category>category</category>"
      + "<line1>line1</line1>"
      + "<line2>line2</line2>"
      + "<townName>townName</townName>"
      + "<postcode>postcode</postcode>"
      + "<latitude>0.0</latitude>"
      + "<longitude>0.0</longitude>"
      + "</address>"
      + "<contactName>contactName</contactName>"
      + "<caseId>1</caseId>";

  private static final String VALIDXML_PART2 = "<questionnaireId>1</questionnaireId>"
      + "<iac>12345678930666392556</iac>"
      + "<events>"
      + "<event>event</event>"
      + "</events>"
      + "</actionRequest>"
      + "<actionRequest>"
      + "<responseRequired>false</responseRequired>"
      + "<actionId>2</actionId>"
      + "<actionPlan>actionPlan</actionPlan>"
      + "<actionType>actionType</actionType>"
      + "<address>"
      + "<uprn>1</uprn>"
      + "<type>type</type>"
      + "<estabType>estabType</estabType>"
      + "<locality>locality</locality>"
      + "<organisationName>organisationName</organisationName>"
      + "<category>category</category>"
      + "<line1>line1</line1>"
      + "<line2>line2</line2>"
      + "<townName>townName</townName>"
      + "<postcode>postcode</postcode>"
      + "<latitude>0.0</latitude>"
      + "<longitude>0.0</longitude>"
      + "</address>"
      + "<contactName>contactName</contactName>"
      + "<caseId>2</caseId>"
      + "<priority>highest</priority>"
      + "<questionnaireId>1</questionnaireId>"
      + "<iac>12345678930666392557</iac>"
      + "<events>"
      + "<event>event</event>"
      + "</events>"
      + "</actionRequest>"
      + "</actionRequests>"
      + "</p:actionInstruction>";

  @Inject
  private MessageChannel testOutbound;

  @Inject
  private ActionExportServiceImpl actionExportService;

  @Inject
  private MessageChannel instructionXml;

  @Inject
  private PollableChannel activeMQDLQXml;

  /**
   * Initialise tests
   *
   * @throws IOException from FileUtils
   */
  @Before
  public void setUpAndInitialVerification() throws IOException {
    File logDir = new File(INVALID_ACTION_INSTRUCTION_LOG_DIRECTORY);
    if (!logDir.exists()) {
      logDir.mkdir();
    }
    FileUtils.cleanDirectory(logDir);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);
  }

  /**
   * SI sent badly formed XML to generate a parse error results in ActiveMQ dead
   * letter queue message. Local transaction should rollback and message should
   * be considered a poisoned bill.
   */
  @Test
  public void testInstructionXmlBadlyFormed() {
    String testMessage = VALIDXML_PART1
        + "<priority>highest<priority>"
        + VALIDXML_PART2;

    testOutbound.send(MessageBuilder.withPayload(testMessage).build());

    Message<?> message = activeMQDLQXml.receive(RECEIVE_TIMEOUT);
    String payload = (String) message.getPayload();
    assertEquals(testMessage, payload);

    File logDir = new File(INVALID_ACTION_INSTRUCTION_LOG_DIRECTORY);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);

  }

  /**
   * Test whole SI flow with valid XML
   *
   * @throws InterruptedException if CountDownLatch interrupted
   */
  @Test
  public void testInstructionXmlValid() throws InterruptedException {
    String testMessage = VALIDXML_PART1
        + "<priority>highest</priority>"
        + VALIDXML_PART2;

    // SetUp CountDownLatch for synchronisation with async call
    final CountDownLatch serviceInvoked = new CountDownLatch(1);
    // Release all waiting threads when mock
    // actionExportService.acceptInstruction method is called
    doAnswer(countsDownLatch(serviceInvoked)).when(actionExportService).acceptInstruction(any());

    // Send message
    testOutbound.send(MessageBuilder.withPayload(testMessage).build());
    // Await synchronisation with the asynchronous message call
    serviceInvoked.await(RECEIVE_TIMEOUT, MILLISECONDS);

    // Test not rejected to instructionXmlInvalid channel
    File logDir = new File(INVALID_ACTION_INSTRUCTION_LOG_DIRECTORY);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);

    ArgumentCaptor<ActionInstruction> argumentCaptor = ArgumentCaptor.forClass(ActionInstruction.class);
    verify(actionExportService).acceptInstruction(argumentCaptor.capture());

    assertEquals(argumentCaptor.getValue().getActionRequests().getActionRequests().get(0).getIac(),
        "12345678930666392556");
    assertEquals(argumentCaptor.getValue().getActionRequests().getActionRequests().get(1).getIac(),
        "12345678930666392557");

  }

  /**
   * Test invalid well formed XML should go to file
   */
  @Test
  public void testInstructionXmlInvalid() {
    String testMessage = VALIDXML_PART1
        + "<priority>NOT VALID VALUE</priority>"
        + VALIDXML_PART2;

    // Send direct to flow rather than JMS queue to avoid problems with
    // asynchronous threads
    instructionXml.send(MessageBuilder.withPayload(testMessage).build());

    File logDir = new File(INVALID_ACTION_INSTRUCTION_LOG_DIRECTORY);
    File[] files = logDir.listFiles();
    assertEquals(1, files.length);
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
}
