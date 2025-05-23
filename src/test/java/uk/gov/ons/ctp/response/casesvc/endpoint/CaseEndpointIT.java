package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.assertj.core.api.Java6Assertions.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.representation.*;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.UnirestInitialiser;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:/application-test.yml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@WireMockTest(httpPort = 18002)
public class CaseEndpointIT {
  private UUID collectionExerciseId;
  private Map<String, String> metadata;
  private PubSubEmulator pubSubEmulator = new PubSubEmulator();

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Autowired private CaseRepository caseRepository;
  @Autowired private CaseEventRepository caseEventRepository;
  @Autowired private CaseGroupRepository caseGroupRepository;

  public CaseEndpointIT() throws IOException {}

  @BeforeClass
  public static void setUp() throws InterruptedException {
    Unirest.config().reset();
    UnirestInitialiser.initialise();
    Thread.sleep(20000);
  }

  @Before
  public void testSetup() {
    pubSubEmulator.testInit();
    caseEventRepository.deleteAll();
    caseRepository.deleteAll();
    caseGroupRepository.deleteAll();
  }

  @After
  public void teardown() {
    pubSubEmulator.testTeardown();
  }

  public void createCollectionData() {
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
  }

  @Test
  public void testCreateSocialCaseEvents() throws Exception {
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    // Given
    caseCreator.postSampleUnit("LMS0002", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.ACTION_CREATED, "SYSTEM", "SOCIALNOT", metadata);

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    // Then
    assertThat(createdCaseResponse.getStatus()).isEqualTo(201);
  }

  @Test
  public void ensureSampleUnitIdReceived() throws Exception {
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    assertThat(casesResponse.getBody()[0].getSampleUnitId().toString())
        .isEqualTo(sampleUnitId.toString());
  }

  /**
   * Test Collection Instrument downloaded case event works with B cases, and case group status has
   * transitioned to InProgress.
   */
  @Test
  public void testCreateCollectionInstrumentDownloadedCaseEventWithBCaseSuccess() throws Exception {
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    // Given
    caseCreator.postSampleUnit("BS12345", "B", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent",
            CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            "SYSTEM",
            "DUMMY",
            metadata);

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .asObject(CaseDetailsDTO.class);

    CaseDetailsDTO affectedCase = returnedCaseResponse.getBody();

    // Then
    assertThat(createdCaseResponse.getStatus()).isEqualTo(201);
    assertThat(affectedCase.getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.INPROGRESS);
  }

  /**
   * Test Collection Instrument downloaded case event works with B cases, and case group status has
   * not transitioned to InProgress.
   */
  @Test
  public void testCreateCollectionInstrumentErrorCaseEventWithBCaseSuccess() throws Exception {
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    // Given
    caseCreator.postSampleUnit("BS12346", "B", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.COLLECTION_INSTRUMENT_ERROR, "SYSTEM", "DUMMY", metadata);

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .asObject(CaseDetailsDTO.class);

    CaseDetailsDTO affectedCase = returnedCaseResponse.getBody();

    // Then
    assertThat(createdCaseResponse.getStatus()).isEqualTo(201);
    assertThat(affectedCase.getCaseGroup().getCaseGroupStatus())
        .isNotEqualTo(CaseGroupStatus.INPROGRESS);
  }

  /**
   * Test Successful response upload case event works with B cases, and case group status has
   * transitioned to Complete.
   */
  @Test
  public void testCreateSuccessfulResponseUploadCaseEventWithBCaseSuccess() throws Exception {
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    // Given
    caseCreator.postSampleUnit("BS12347", "B", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, "SYSTEM", "DUMMY", metadata);

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .asObject(CaseDetailsDTO.class);

    CaseDetailsDTO affectedCase = returnedCaseResponse.getBody();

    // Then
    assertThat(createdCaseResponse.getStatus()).isEqualTo(201);
    assertThat(affectedCase.getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.COMPLETE);
  }

  /**
   * Test Unsuccessful Response Upload case event works with B cases, and case group status has not
   * transitioned to Complete.
   */
  @Test
  public void testGetCaseEventsWithCategory() throws Exception {
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    // Given
    caseCreator.postSampleUnit("BS12348", "B", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, "SYSTEM", "DUMMY", metadata);

    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    // When
    HttpResponse<CaseEventDTO[]> returnedCaseEventsResponse =
        Unirest.get(
                "http://localhost:"
                    + port
                    + "/cases/"
                    + caseID
                    + "/events?category=SUCCESSFUL_"
                    + "RESPONSE_UPLOAD")
            .basicAuth("admin", "secret")
            .asObject(CaseEventDTO[].class);

    CaseEventDTO[] returnedCaseEvents = returnedCaseEventsResponse.getBody();

    // Then
    assertThat(returnedCaseEventsResponse.getStatus()).isEqualTo(200);
    assertThat(returnedCaseEvents.length).isEqualTo(1);
    assertThat(returnedCaseEvents[0].getCategory())
        .isEqualTo(CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);
    assertThat(returnedCaseEvents[0].getCreatedDateTime()).isNotNull();
  }

  @Test
  public void testGetCaseEventsWithCategoryMissingCaseShouldFail() throws Exception {
    // Given
    createCollectionData();
    // When
    HttpResponse returnedCaseEventsResponse =
        Unirest.get(
                "http://localhost:"
                    + port
                    + "/cases/"
                    + UUID.randomUUID()
                    + "/events?category=SUCCESSFUL_"
                    + "RESPONSE_UPLOAD")
            .basicAuth("admin", "secret")
            .asString();
    // Then
    assertThat(returnedCaseEventsResponse.getStatus()).isEqualTo(404);
  }

  @Test
  public void testGetCaseEventsWithNonExistentCategory() throws Exception {
    // Given
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("BS12349", "B", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, "SYSTEM", "DUMMY", metadata);

    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    // When
    HttpResponse returnedCaseEventsResponse =
        Unirest.get(
                "http://localhost:"
                    + port
                    + "/cases/"
                    + caseID
                    + "/events?category="
                    + "FAKE_CATEGORY_NAME")
            .basicAuth("admin", "secret")
            .asString();

    // Then
    assertThat(returnedCaseEventsResponse.getStatus()).isEqualTo(400);
  }

  @Test
  public void testGetNoCaseEventsWithCategory() throws Exception {
    // Given
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("BS12350", "B", sampleUnitId, collectionExerciseId);
    Thread.sleep(2000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, "SYSTEM", "DUMMY", metadata);

    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO)
            .asObject(CreatedCaseEventDTO.class);

    // When
    HttpResponse returnedCaseEventsResponse =
        Unirest.get(
                "http://localhost:"
                    + port
                    + "/cases/"
                    + caseID
                    + "/events?category="
                    + "OFFLINE_RESPONSE_PROCESSED")
            .basicAuth("admin", "secret")
            .asString();

    // Then
    assertThat(returnedCaseEventsResponse.getStatus()).isEqualTo(204);
  }

  private HttpResponse<CaseDetailsDTO[]> getCreatedCase(UUID sampleUnitId) throws Exception {
    return Unirest.get(String.format("http://localhost:%d/cases/sampleunitids", port))
        .basicAuth("admin", "secret")
        .queryString("sampleUnitId", sampleUnitId)
        .header("Content-Type", "application/json")
        .asObject(CaseDetailsDTO[].class);
  }
}
