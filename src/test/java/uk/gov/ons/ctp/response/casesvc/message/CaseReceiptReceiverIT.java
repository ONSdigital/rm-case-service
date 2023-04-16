package uk.gov.ons.ctp.response.casesvc.message;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.PubSubTestEmulator;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:/application-test.yml")
public class CaseReceiptReceiverIT {

  private final PubSubTestEmulator pubSubEmulator = new PubSubTestEmulator();

  public CaseReceiptReceiverIT() throws IOException {}

  @Before
  public void testSetup() {
    pubSubEmulator.testInit();
  }

  @After
  public void testTearDown() {
    pubSubEmulator.testTeardown();
  }

  @Test
  public void testCaseNotificationReceiverIsReceivingMessageFromPubSub() throws Exception {
    // Publish a test receipt
    String receiptFile =
        "src/test/resources/uk/gov/ons/ctp/response/casesvc/message/receiptPubsub.json";
    String json = readFileAsString(receiptFile);
    pubSubEmulator.publishMessage(json);

    // Call the receiver to get the receipt message
    TestPubSubMessage message = new TestPubSubMessage();
    Thread.sleep(2000);
    CaseReceipt caseReceipt = message.getCaseReceipt();

    // Test the case ID of the receipt received is the one we published
    Assert.assertEquals(caseReceipt.getCaseId(), "9b872c6c-3339-4db9-9ef6-ff37a4446321");
  }

  private static String readFileAsString(String file) throws Exception {
    return new String(Files.readAllBytes(Paths.get(file)));
  }
}
