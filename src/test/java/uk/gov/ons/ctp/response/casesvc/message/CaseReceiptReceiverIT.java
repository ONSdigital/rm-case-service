package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:/application-test.yml")
public class CaseReceiptReceiverIT {

  private static PubSubEmulator PUBSUBEMULATOR;

  private String receiptFile =
      "src/test/resources/uk/gov/ons/ctp/response/casesvc/message/receiptPubsub.json";

  @ClassRule
  public static final EnvironmentVariables environmentVariables =
      new EnvironmentVariables().set("PUBSUB_EMULATOR_HOST", "127.0.0.1:18681");

  @ClassRule public static WireMockRule wireMockRule = new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @MockBean private CaseReceiptReceiver caseReceiptReceiver;

  public CaseReceiptReceiverIT() throws IOException {}

  @BeforeClass
  public static void testSetup() throws IOException {
    PUBSUBEMULATOR = new PubSubEmulator();
    PUBSUBEMULATOR.testInit();
  }

  @AfterClass
  public static void testTearDown() {
    PUBSUBEMULATOR.testTeardown();
  }

  @Test
  public void testCaseNotificationReceiverIsReceivingMessageFromPubSub() throws Exception {
    String json = readFileAsString(receiptFile);
    PUBSUBEMULATOR.publishMessage(json);
    Thread.sleep(2000);
    ObjectMapper objectMapper = new ObjectMapper();
    CaseReceipt caseReceipt = objectMapper.readValue(json, CaseReceipt.class);

    Mockito.verify(caseReceiptReceiver, Mockito.times(1)).process(caseReceipt);
  }

  private static String readFileAsString(String file) throws Exception {
    return new String(Files.readAllBytes(Paths.get(file)));
  }
}
