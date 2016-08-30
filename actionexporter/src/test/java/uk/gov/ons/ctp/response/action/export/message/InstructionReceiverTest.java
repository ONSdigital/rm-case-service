package uk.gov.ons.ctp.response.action.export.message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import javax.jms.JMSException;

import org.apache.commons.io.FileUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * What have we here - oh - another test. Test we can receive feedback
 * @author centos
 *
 */
@ContextConfiguration(locations = { "/PrintServiceTest-context.xml" })
@TestPropertySource("classpath:/application-test.properties")
@RunWith(SpringJUnit4ClassRunner.class)
public class InstructionReceiverTest {

  @Autowired
  private MessageChannel instructionXml;

  
  @Autowired
  @Qualifier("instructionUnmarshaller")
  private Jaxb2Marshaller instructionUnmarshaller;

  private static final String INVALID_ACTION_FEEDBACK_LOG_DIRECTORY_NAME
    = "/tmp/ctp/logs/printsvc/instruction";
  private static final String PACKAGE_ACTION_INSTRUCTION
     = "uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction";

  /**
   * A Test
   * @throws Exception oops
   */
  @Before
  public void setUpAndInitialVerification() throws Exception {
    File logDir = new File(INVALID_ACTION_FEEDBACK_LOG_DIRECTORY_NAME);
    if (!logDir.exists()) {
      logDir.mkdir();
    }
    FileUtils.cleanDirectory(logDir);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length);

    
    
    String jaxbContext = instructionUnmarshaller.getJaxbContext().toString();
    assertTrue(jaxbContext.contains(PACKAGE_ACTION_INSTRUCTION));
  }

  @After
  public void finishCleanly() throws IOException, JMSException {
  }

  /**
   * A Test
   */
//  @Test
  public void testSendActiveMQValidMessage() {
    String testMessage = 
"<p:actionInstruction"
	+" xmlns:p=\"http://ons.gov.uk/ctp/response/action/message/instruction\" "
	+" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" "
	+" xsi:schemaLocation=\"http://ons.gov.uk/ctp/response/action/message/instruction actionInstruction.xsd \">"
 +" <actionRequests>\n"
 +"   <actionRequest>\n"
 +"     <responseRequired>false</responseRequired>\n"
 +"     <actionId>201</actionId>\n"
 +"     <actionType>string</actionType>\n"
 +"     <address>\n"
 +"       <uprn>201</uprn>\n"
 +"       <type>string</type>\n"
 +"       <estabType>string</estabType>\n"
 +"       <locality>string</locality>\n"
 +"       <organisationName>string</organisationName>\n"
 +"       <category>string</category>\n"
 +"       <line1>string</line1>\n"
 +"       <line2>string</line2>\n"
 +"       <townName>string</townName>\n"
 +"       <postcode>string</postcode>\n"
 +"       <latitude>12</latitude>\n"
 +"       <longitude>1000.00</longitude>\n"
 +"     </address>\n"
 +"     <contactName>string</contactName>\n"
 +"     <caseId>201</caseId>\n"
 +"     <priority>lower</priority>\n"
 +"     <questionnaireId>201</questionnaireId>\n"
 +"     <iac>string</iac>\n"
 +"     <events>\n"
 +"       <event>string</event>\n"
 +"     </events>\n"
 +"   </actionRequest>\n"
 +" </actionRequests>\n"
+"</p:actionInstruction>";
    instructionXml.send(MessageBuilder.withPayload(testMessage).build());

    File logDir = new File(INVALID_ACTION_FEEDBACK_LOG_DIRECTORY_NAME);
    File[] files = logDir.listFiles();
    assertEquals(0, files.length); // This validates the xml testMessage was
                                   // deemed OK.

    /**
     * The message above is picked up by FeedbackReceiverImpl. This can be
     * verified putting a debug point at the entrance of acceptFeedback.
     */
    // TODO further assertions once acceptFeedback has been implemented
  }

  /**
   * A Test
   * @throws IOException darn it
   */
  @Test
  public void testSendActiveMQInvalidMessage() throws IOException {
    String testMessage = 
 "<ins:actionInstruction xmlns:ins=\"http://ons.gov.uk/ctp/response/action/message/instruction\">\n"
 +" <dodgyactionRequests>\n"
 +"   <actionRequest>\n"
 +"     <responseRequired>false</responseRequired>\n"
 +"     <actionId>201</actionId>\n"
 +"     <actionType>string</actionType>\n"
 +"     <address>\n"
 +"       <uprn>201</uprn>\n"
 +"       <type>string</type>\n"
 +"       <estabType>string</estabType>\n"
 +"       <locality>string</locality>\n"
 +"       <organisationName>string</organisationName>\n"
 +"       <category>string</category>\n"
 +"       <line1>string</line1>\n"
 +"       <line2>string</line2>\n"
 +"       <townName>string</townName>\n"
 +"       <postcode>string</postcode>\n"
 +"       <latitude>12</latitude>\n"
 +"       <longitude>1000.00</longitude>\n"
 +"     </address>\n"
 +"     <contactName>string</contactName>\n"
 +"     <caseId>201</caseId>\n"
 +"     <priority>lower</priority>\n"
 +"     <questionnaireId>201</questionnaireId>\n"
 +"     <iac>string</iac>\n"
 +"     <events>\n"
 +"       <event>string</event>\n"
 +"     </events>\n"
 +"   </actionRequest>\n"
 +" </dodgyactionRequests>\n"
+"</ins:actionInstruction>";
    instructionXml.send(MessageBuilder.withPayload(testMessage).build());

    // expect one file to be added to the log folder
    File logDir = new File(INVALID_ACTION_FEEDBACK_LOG_DIRECTORY_NAME);
    File[] files = logDir.listFiles();
    assertEquals(1, files.length);
  }
}
