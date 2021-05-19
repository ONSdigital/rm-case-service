package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.MessageChannel;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.IACServiceStub;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.representation.*;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.UnirestInitialiser;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CaseReceiptReceiverIT {

  private UUID collectionExerciseId;

  private PubSubEmulator pubSubEmulator = new PubSubEmulator();
  private String receiptFile =
      "src/test/resources/uk/gov/ons/ctp/response/casesvc/message/receiptPubsub.json";

  @ClassRule
  public static final EnvironmentVariables environmentVariables =
      new EnvironmentVariables().set("PUBSUB_EMULATOR_HOST", "127.0.0.1:18681");

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @Autowired private MessageChannel caseReceiptTransformed;

  @MockBean private CaseReceiptReceiver caseReceiptReceiver;

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private IACServiceStub iacServiceStub;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;

  public CaseReceiptReceiverIT() throws IOException {}

  @BeforeClass
  public static void setUp() throws InterruptedException {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
    Thread.sleep(2000);
  }

  @Before
  public void testSetup() {
    pubSubEmulator.testInit();
    Random rnd = new Random();

    int randNumber = 10000 + rnd.nextInt(900000);

    UUID surveyId = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");

    collectionExerciseSvcClient.createCollectionExercise(
        surveyId, Integer.toString(randNumber), "January 2018");

    CollectionExerciseDTO collex =
        collectionExerciseSvcClient.getCollectionExercises(surveyId.toString()).get(0);

    collectionExerciseId = collex.getId();
  }

  @After
  public void testTearDown() {
    pubSubEmulator.testTeardown();
  }

  @Test
  public void testCaseNotificationReceiverIsReceivingMessageFromPubSub() throws Exception {
    String json = readFileAsString(receiptFile);
    pubSubEmulator.publishMessage(json);
    Thread.sleep(2000);
    ObjectMapper objectMapper = new ObjectMapper();
    CaseReceipt caseReceipt = objectMapper.readValue(json, CaseReceipt.class);

    // For now the inboundChannel is added before it gets to 'process' to emulate current behaviour
    // in rabbit
    caseReceipt.setInboundChannel(InboundChannel.ONLINE);
    Mockito.verify(caseReceiptReceiver, Mockito.times(1)).process(caseReceipt);
  }

  private static String readFileAsString(String file) throws Exception {
    return new String(Files.readAllBytes(Paths.get(file)));
  }
}
