package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestException;
import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
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
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseIACDTO;
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
public class CaseIACEndpointIT {
  private UUID collectionExerciseId;
  private Map<String, String> metadata;

  private static final Logger log = LoggerFactory.getLogger(CaseIACEndpointIT.class);
  private PubSubEmulator pubSubEmulator = new PubSubEmulator();

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Autowired private CaseRepository caseRepository;
  @Autowired private CaseEventRepository caseEventRepository;
  @Autowired private CaseGroupRepository caseGroupRepository;

  public CaseIACEndpointIT() throws IOException {}

  @BeforeClass
  public static void setUp() throws InterruptedException {
    Unirest.config().reset();
    UnirestInitialiser.initialise();
    Thread.sleep(20000);
  }

  @Before
  public void testSetup() throws Exception {
    pubSubEmulator.testInit();
    caseEventRepository.deleteAll();
    caseRepository.deleteAll();
    caseGroupRepository.deleteAll();
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

  @After
  public void teardown() {
    pubSubEmulator.testTeardown();
  }

  @Test
  public void shouldCreateNewIACCode() throws Exception {
    // Given
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(3000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    String notExpected = getCurrentIACCode(caseID);

    // When
    HttpResponse<String> actual = generateNewIACCode(caseID);

    // Then
    assertThat(actual.getStatus(), is(equalTo(HttpStatus.CREATED.value()))); // assert IAC in model
    assertThat(actual.getBody(), not(equalTo(notExpected)));
  }

  @Test
  public void shouldGetIacCodes() throws Exception {
    // Given
    createCollectionData();
    UUID sampleUnitId = UUID.randomUUID();
    caseCreator.postSampleUnit("LMS0001", "H", sampleUnitId, collectionExerciseId);
    Thread.sleep(3000);
    HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = casesResponse.getBody()[0].getId().toString();
    // When
    HttpResponse<CaseIACDTO[]> iacs =
        Unirest.get("http://localhost:{port}/cases/{caseId}/iac")
            .routeParam("port", Integer.toString(port))
            .routeParam("caseId", caseID)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseIACDTO[].class);

    assertThat(iacs.getBody(), arrayWithSize(1));
    assertNotNull(iacs.getBody()[0]);
  }

  private String getCurrentIACCode(String caseId) throws UnirestException {
    HttpResponse<CaseDetailsDTO> caseDetails =
        Unirest.get("http://localhost:{port}/cases/{caseId}")
            .routeParam("port", Integer.toString(port))
            .routeParam("caseId", caseId)
            .queryString("iac", true)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO.class);

    return caseDetails.getBody().getIac();
  }

  private HttpResponse<String> generateNewIACCode(String caseId) throws UnirestException {
    return Unirest.post("http://localhost:{port}/cases/{caseId}/iac")
        .routeParam("port", Integer.toString(port))
        .routeParam("caseId", caseId)
        .basicAuth("admin", "secret")
        .header("Content-Type", "application/json")
        .asString();
  }

  private HttpResponse<CaseDetailsDTO[]> getCreatedCase(UUID sampleUnitId) {
    return Unirest.get(String.format("http://localhost:%d/cases/sampleunitids", port))
        .basicAuth("admin", "secret")
        .queryString("sampleUnitId", sampleUnitId)
        .header("Content-Type", "application/json")
        .asObject(CaseDetailsDTO[].class);
  }
}
