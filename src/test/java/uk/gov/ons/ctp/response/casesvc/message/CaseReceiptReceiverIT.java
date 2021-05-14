package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.io.IOException;
import java.util.Random;
import java.util.UUID;
import org.junit.*;
import org.junit.contrib.java.lang.system.EnvironmentVariables;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
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

  @ClassRule
  public static final EnvironmentVariables environmentVariables =
      new EnvironmentVariables().set("PUBSUB_EMULATOR_HOST", "127.0.0.1:18681");

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @Autowired private MessageChannel caseReceiptTransformed;

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
    Random rnd = new Random();

    int randNumber = 10000 + rnd.nextInt(900000);

    UUID surveyId = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");

    collectionExerciseSvcClient.createCollectionExercise(
        surveyId, Integer.toString(randNumber), "January 2018");

    CollectionExerciseDTO collex =
        collectionExerciseSvcClient.getCollectionExercises(surveyId.toString()).get(0);

    collectionExerciseId = collex.getId();
  }

  @Test
  @Ignore
  public void socialCaseShouldReceipt() throws Exception {
    pubSubEmulator.testInit();
    TestPubSubMessage pubSubMessage = new TestPubSubMessage();
    // Given
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0003", "H", sampleUnitId, collectionExerciseId);
    CaseNotificationDTO caseNotificationDTO = pubSubMessage.getPubSubCaseNotification();
    startCase(caseNotificationDTO.getCaseId());
    CaseReceipt caseReceipt =
        new CaseReceipt(
            "caseRef", caseNotificationDTO.getCaseId(), InboundChannel.OFFLINE, "partyId");
    Message<CaseReceipt> message = new GenericMessage<>(caseReceipt);
    iacServiceStub.disableIACStub();

    // When
    caseReceiptTransformed.send(message);

    // Then
    HttpResponse<CaseDetailsDTO[]> casesResponse =
        Unirest.get(String.format("http://localhost:%d/cases/sampleunitids", port))
            .basicAuth("admin", "secret")
            .queryString("sampleUnitId", sampleUnitId)
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO[].class);
    CaseDetailsDTO caseDetailsDTO = casesResponse.getBody()[0];
    assertEquals(CaseGroupStatus.COMPLETE, caseDetailsDTO.getCaseGroup().getCaseGroupStatus());
    assertEquals(1, caseDetailsDTO.getResponses().size());
    assertEquals("OFFLINE", caseDetailsDTO.getResponses().get(0).getInboundChannel());
    pubSubEmulator.testTeardown();
  }

  private void startCase(String caseId) throws Exception {
    CaseEventCreationRequestDTO caseEvent = new CaseEventCreationRequestDTO();
    caseEvent.setCategory(CategoryDTO.CategoryName.EQ_LAUNCH);
    caseEvent.setCreatedBy("test");
    caseEvent.setDescription("test");
    Unirest.post(String.format("http://localhost:%d/cases/%s/events", port, caseId))
        .basicAuth("admin", "secret")
        .header("Content-Type", "application/json")
        .body(caseEvent)
        .asObject(CreatedCaseEventDTO.class);
  }
}
