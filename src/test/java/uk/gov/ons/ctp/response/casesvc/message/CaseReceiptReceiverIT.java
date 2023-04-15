package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.*;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
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

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @MockBean private CaseReceiptReceiver caseReceiptReceiver;

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
    String receiptFile =
        "src/test/resources/uk/gov/ons/ctp/response/casesvc/message/receiptPubsub.json";
    String json = readFileAsString(receiptFile);
    System.out.println("************PUBLISHING****************");
    pubSubEmulator.publishMessage(json);
    System.out.println("^^^^^^^^^^^^PUBLISHED^^^^^^^^^^^^^^^^");
    Thread.sleep(2000);
    ObjectMapper objectMapper = new ObjectMapper();
    CaseReceipt caseReceipt = objectMapper.readValue(json, CaseReceipt.class);

    Mockito.verify(caseReceiptReceiver, Mockito.times(1)).process(caseReceipt);
  }

  private static String readFileAsString(String file) throws Exception {
    return new String(Files.readAllBytes(Paths.get(file)));
  }
}
