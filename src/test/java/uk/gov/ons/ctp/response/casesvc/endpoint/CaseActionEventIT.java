package uk.gov.ons.ctp.response.casesvc.endpoint;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.io.IOException;
import java.time.Instant;
import java.util.*;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionEventRequest;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionEventRequestRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionEvent;
import uk.gov.ons.ctp.response.casesvc.service.action.email.ProcessEmailActionService;
import uk.gov.ons.ctp.response.casesvc.service.action.letter.ProcessLetterActionService;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.UnirestInitialiser;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(
    locations = "classpath:/application-test.yml",
    properties = "action-svc.deprecated=true")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CaseActionEventIT {
  private UUID collectionExerciseId;
  private Map<String, String> metadata;
  private PubSubEmulator pubSubEmulator = new PubSubEmulator();

  @MockBean private ProcessLetterActionService processLetterActionService;
  @MockBean private ProcessEmailActionService processEmailActionService;

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Autowired private CaseRepository caseRepository;
  @Autowired private CaseEventRepository caseEventRepository;
  @Autowired private CaseActionEventRequestRepository actionEventRequestRepository;

  public CaseActionEventIT() throws IOException {}

  @BeforeClass
  public static void setUp() throws InterruptedException {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
    Thread.sleep(2000);
  }

  @Before
  public void testSetup() throws InterruptedException {
    pubSubEmulator.testInit();
    caseEventRepository.deleteAll();
    caseRepository.deleteAll();
    actionEventRequestRepository.deleteAll();
  }

  @After
  public void teardown() {
    pubSubEmulator.testTeardown();
  }

  public UUID createCollectionData() {
    Random rnd = new Random();

    int randNumber = 10000 + rnd.nextInt(900000);

    UUID surveyId = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");

    collectionExerciseSvcClient.createCollectionExercise(
        surveyId, Integer.toString(randNumber), "January 2018");

    CollectionExerciseDTO collex =
        collectionExerciseSvcClient.getCollectionExercises(surveyId.toString()).get(0);

    collectionExerciseId = collex.getId();
    metadata = new HashMap<>();
    metadata.put("partyId", UUID.randomUUID().toString());
    return collectionExerciseId;
  }

  @Test
  public void testEmailEventIsProcessedProperly() throws Exception {
    Mockito.when(
            processEmailActionService.processEmailService(
                any(CollectionExerciseDTO.class), anyString(), any(Instant.class)))
        .thenReturn(new AsyncResult<>(true));
    Mockito.when(
            processLetterActionService.processLetterService(
                any(CollectionExerciseDTO.class), anyString(), any(Instant.class)))
        .thenReturn(new AsyncResult<>(true));
    UUID collectionExerciseId = createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    // When
    CaseActionEvent caseActionEvent = new CaseActionEvent();
    caseActionEvent.setCollectionExerciseID(collectionExerciseId);
    caseActionEvent.setTag(CaseActionEvent.EventTag.go_live);
    HttpResponse processEventResponse =
        Unirest.post("http://localhost:" + port + "/process-event")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseActionEvent)
            .asString();
    Assert.assertEquals(202, processEventResponse.getStatus());
    Thread.sleep(5000);
    List<CaseActionEventRequest> caseActionEventRequestList =
        actionEventRequestRepository.findByCollectionExerciseIdAndEventTag(
            collectionExerciseId, "go_live");
    Assert.assertEquals(1, caseActionEventRequestList.size());
    Assert.assertEquals(
        CaseActionEventRequest.ActionEventRequestStatus.COMPLETED,
        caseActionEventRequestList.get(0).getStatus());
  }

  @Test
  public void testLetterEventIsProcessedProperly() throws Exception {
    Mockito.when(
            processLetterActionService.processLetterService(
                any(CollectionExerciseDTO.class), anyString(), any(Instant.class)))
        .thenReturn(new AsyncResult<>(true));
    Mockito.when(
            processEmailActionService.processEmailService(
                any(CollectionExerciseDTO.class), anyString(), any(Instant.class)))
        .thenReturn(new AsyncResult<>(true));
    UUID collectionExerciseId = createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    // When
    CaseActionEvent caseActionEvent = new CaseActionEvent();
    caseActionEvent.setCollectionExerciseID(collectionExerciseId);
    caseActionEvent.setTag(CaseActionEvent.EventTag.mps);
    HttpResponse processEventResponse =
        Unirest.post("http://localhost:" + port + "/process-event")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseActionEvent)
            .asString();
    Assert.assertEquals(202, processEventResponse.getStatus());
    Thread.sleep(5000);
    List<CaseActionEventRequest> caseActionEventRequestList =
        actionEventRequestRepository.findByCollectionExerciseIdAndEventTag(
            collectionExerciseId, "mps");
    Assert.assertEquals(1, caseActionEventRequestList.size());
    Assert.assertEquals(
        CaseActionEventRequest.ActionEventRequestStatus.COMPLETED,
        caseActionEventRequestList.get(0).getStatus());
  }

  @Test
  public void testProcessEventFailureScenario() throws Exception {
    Mockito.when(
            processEmailActionService.processEmailService(
                any(CollectionExerciseDTO.class), anyString(), any(Instant.class)))
        .thenReturn(new AsyncResult<>(false));
    Mockito.when(
            processLetterActionService.processLetterService(
                any(CollectionExerciseDTO.class), anyString(), any(Instant.class)))
        .thenReturn(new AsyncResult<>(true));
    UUID collectionExerciseId = createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    // When
    CaseActionEvent caseActionEvent = new CaseActionEvent();
    caseActionEvent.setCollectionExerciseID(collectionExerciseId);
    caseActionEvent.setTag(CaseActionEvent.EventTag.go_live);
    HttpResponse processEventResponse =
        Unirest.post("http://localhost:" + port + "/process-event")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseActionEvent)
            .asString();
    Assert.assertEquals(202, processEventResponse.getStatus());
    Thread.sleep(5000);
    List<CaseActionEventRequest> caseActionEventRequestList =
        actionEventRequestRepository.findByCollectionExerciseIdAndEventTag(
            collectionExerciseId, "go_live");
    Assert.assertEquals(1, caseActionEventRequestList.size());
    Assert.assertEquals(
        CaseActionEventRequest.ActionEventRequestStatus.RETRY,
        caseActionEventRequestList.get(0).getStatus());
    HttpResponse retryEventResponse =
        Unirest.post("http://localhost:" + port + "/retry-event")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseActionEvent)
            .asString();
    Assert.assertEquals(202, retryEventResponse.getStatus());
    Thread.sleep(5000);
    List<CaseActionEventRequest> retryCaseActionEventRequestList =
        actionEventRequestRepository.findByCollectionExerciseIdAndEventTag(
            collectionExerciseId, "go_live");
    Assert.assertEquals(1, retryCaseActionEventRequestList.size());
    Assert.assertEquals(
        CaseActionEventRequest.ActionEventRequestStatus.FAILED,
        retryCaseActionEventRequestList.get(0).getStatus());
  }
}
