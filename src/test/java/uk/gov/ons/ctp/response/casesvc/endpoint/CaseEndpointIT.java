package uk.gov.ons.ctp.response.casesvc.endpoint;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Java6Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import org.junit.*;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.representation.CreatedCaseEventDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class CaseEndpointIT {

  private UUID collectionExerciseId;
  private Map<String, String> metadata;

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Autowired private CaseRepository caseRepository;
  @Autowired private CaseEventRepository caseEventRepository;

  @BeforeClass
  public static void setUp() throws InterruptedException {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
    Thread.sleep(2000);
  }

  @Before
  public void testSetup() {
    caseEventRepository.deleteAll();
    caseRepository.deleteAll();

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
  public void ensureSampleUnitIdReceived() throws Exception {
    UUID sampleUnitId = UUID.randomUUID();

    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);

    assertThat(caseNotification.getSampleUnitId()).isEqualTo(sampleUnitId.toString());
  }

  @Test
  public void testCreateSocialCaseEvents() throws Exception {

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("LMS0002", "H", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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
  public void ensureCaseReturnedBySampleUnitId() throws Exception {

    UUID sampleUnitId = UUID.randomUUID();
    CaseNotification caseNotif =
        caseCreator.sendSampleUnit("LMS0003", "H", sampleUnitId, collectionExerciseId);

    UUID caseId = UUID.fromString(caseNotif.getCaseId());

    HttpResponse<CaseDetailsDTO[]> casesResponse =
        Unirest.get(String.format("http://localhost:%d/cases/sampleunitids", port))
            .basicAuth("admin", "secret")
            .queryString("sampleUnitId", sampleUnitId)
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO[].class);

    UUID returnedCaseId = casesResponse.getBody()[0].getId();

    assertThat(returnedCaseId).isEqualTo(caseId);
  }

  /**
   * Test Collection Instrument downloaded case event works with B cases, and case group status has
   * transitioned to InProgress.
   */
  @Test
  public void testCreateCollectionInstrumentDownloadedCaseEventWithBCaseSuccess() throws Exception {

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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
  public void testCreateUnsuccessfulResponseUploadCaseEventWithBCaseSuccess() throws Exception {

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO(
            "TestEvent", CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD, "SYSTEM", "DUMMY", metadata);

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
        .isNotEqualTo(CaseGroupStatus.COMPLETE);
  }

  @Test
  public void testGetCaseEventsWithCategory() throws Exception {

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID(), collectionExerciseId);

    String caseID = caseNotification.getCaseId();
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
}
