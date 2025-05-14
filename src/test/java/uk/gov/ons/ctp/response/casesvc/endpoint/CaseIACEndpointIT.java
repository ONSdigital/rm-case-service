package uk.gov.ons.ctp.response.casesvc.endpoint;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
//import com.godaddy.logging.Logger;
//import com.godaddy.logging.LoggerFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.IOException;
import java.util.*;

import org.junit.*;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestClient;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseIACDTO;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@TestPropertySource(locations = "classpath:/application-test.yml")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
// TODO: Java 21 Sprint Boot 3: Fix github.tomakehurst.wiremock
public class CaseIACEndpointIT {
  private UUID collectionExerciseId;
  private Map<String, String> metadata;

  //private static final Logger log = LoggerFactory.getLogger(CaseIACEndpointIT.class);
  private PubSubEmulator pubSubEmulator = new PubSubEmulator();

  @ClassRule public static WireMockRule wireMockRule = new WireMockRule(options().port(18002));

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Autowired private CaseRepository caseRepository;
  @Autowired private CaseEventRepository caseEventRepository;
  @Autowired private CaseGroupRepository caseGroupRepository;
  @Autowired private TestRestTemplate testRestTemplate;

  public CaseIACEndpointIT() throws IOException {}

  @BeforeClass
  public static void setUp() throws InterruptedException {
    ObjectMapper value = new ObjectMapper();
   // UnirestInitialiser.initialise(value);
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
    String caseID = String.valueOf(getCreatedCaseId(sampleUnitId));
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
    //HttpResponse<CaseDetailsDTO[]> casesResponse = getCreatedCase(sampleUnitId);
    String caseID = String.valueOf(getCreatedCaseId(sampleUnitId));
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

  private UUID getCreatedCaseId(UUID sampleUnitId) throws Exception {
    // TODO: Could not write JSON: Class versions V1_5 or less must use F_NEW frames
    // We are trying a different REST client (Spring's TestRestTemplate) to see if we see the same issue
    // as with the old Unirest client (which we do) so it doesn't seem to be an issue with the test request.
    // the problem appears to be within the Spring webenvironment server:
    // @SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
    //  which internally throws an exception when returning the response (sadly that exception is our friend we see in the GoDaddy/ASM logging)
    // {"error":{"code":"SYSTEM_ERROR","timestamp":"20241101092339701","message":"Could not write JSON: Class versions V1_5 or less must use F_NEW frames."}}
    // so something fundamental is going on with Spring and Java bytecode reflection.
    // Worth saying our own code is working ok, and the endpoint gets invoked and does indeed yield the correct case details
    // however, the Spring testing framework server is not happy and can't render the HTTP response
    testRestTemplate.withBasicAuth("admin","secret");
    System.out.println("**** HERE 1 ****");
    // The service returns a JSON array of CaseDetailsDTO objects, but we only need the first one
    ResponseEntity<String> jsonString = testRestTemplate.getForEntity(
            String.format("http://localhost:%d/cases/sampleunitids?sampleUnitId=%s", port, sampleUnitId),
            String.class);
    System.out.println("**** HERE 2 ****");
//    ResponseEntity<JsonNode> jsonNode = testRestTemplate.getForEntity(
//            String.format("http://localhost:%d/cases/sampleunitids?sampleUnitId=%s", port, sampleUnitId),
//            JsonNode.class);

    System.out.println(jsonString.getBody());

    ObjectMapper objectMapper = new ObjectMapper();
    List<CaseDetailsDTO> caseDetailsDTOs = objectMapper.readValue(jsonString.getBody(),
            objectMapper.getTypeFactory().constructCollectionType(List.class, CaseDetailsDTO.class));

    System.out.println("*************** response:::::" + caseDetailsDTOs.get(0));
//    RestClient restClient = RestClient.builder()
//            .baseUrl(String.format("http://localhost:%d/cases/sampleunitids", port))
//            .defaultHeaders(
//                    httpHeaders -> {
//                      httpHeaders.setBasicAuth("admin", "secret");
//                      httpHeaders.set("Content-Type", "application/json");
//                    }
//            ).build();
//
//    String data = restClient.get()
//            .uri("?sampleUnitId=%s",sampleUnitId)
//            .accept(MediaType.APPLICATION_JSON)
//            .retrieve()
//            .body(String.class);
//   System.out.println(data);

      // This is the original REST client call that was replaced by the above code
//    HttpResponse<CaseDetailsDTO[]> casesResponse =
//            Unirest.get(String.format("http://localhost:%d/cases/sampleunitids", port))
//                    .basicAuth("admin", "secret")
//                    .queryString("sampleUnitId", sampleUnitId)
//                    .header("Content-Type", "application/json")
//                    .asObject(CaseDetailsDTO[].class);

//    return casesResponse

    return caseDetailsDTOs.get(0).getId();
  }
}
