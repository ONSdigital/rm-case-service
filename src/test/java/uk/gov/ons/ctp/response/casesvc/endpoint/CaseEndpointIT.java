package uk.gov.ons.ctp.response.casesvc.endpoint;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.post;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Java6Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import javax.xml.bind.JAXBContext;
import lombok.extern.slf4j.Slf4j;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.rules.SpringClassRule;
import org.springframework.test.context.junit4.rules.SpringMethodRule;
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.representation.CreatedCaseEventDTO;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageBase;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

@Slf4j
@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CaseEndpointIT {

  @ClassRule
  public static final SpringClassRule SPRING_CLASS_RULE = new SpringClassRule();
  
  @Rule
  public final SpringMethodRule springMethodRule = new SpringMethodRule();
  
  @Rule
  public WireMockRule wireMockRule = new WireMockRule(options().port(18002));
  
  @Autowired
  private ResourceLoader resourceLoader;
  
  @LocalServerPort
  private int port;
  
  @Autowired
  private ObjectMapper mapper;
  
  @Autowired
  private AppConfig appConfig;

  @BeforeClass
  public static void setUp() {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
  }

  @Test
  public void ensureSampleUnitIdReceived() throws Exception {
    UUID sampleUnitId = UUID.randomUUID();

    CaseNotification caseNotification = sendSampleUnit("LMS0001", "H", sampleUnitId);

    assertThat(caseNotification.getSampleUnitId()).isEqualTo(sampleUnitId.toString());
  }

  @Test
  public void testCreateSocialCaseEvents() throws Exception {

    // Given
    CaseNotification caseNotification = sendSampleUnit("LMS0002", "H", UUID.randomUUID());

    String caseID = caseNotification.getCaseId();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO = new CaseEventCreationRequestDTO(
        "TestEvent", CategoryName.ACTION_CREATED, "SYSTEM", "SOCIALNOT", null);

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret").header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO).asObject(CreatedCaseEventDTO.class);

    // Then
    assertThat(createdCaseResponse.getStatus()).isEqualTo(201);
  }

  @Test
  public void ensureCaseReturnedBySampleUnitId() throws Exception {

    UUID sampleUnitId = UUID.randomUUID();
    CaseNotification caseNotif = sendSampleUnit("LMS0003", "H", sampleUnitId);

    UUID caseId = UUID.fromString(caseNotif.getCaseId());

    HttpResponse<CaseDetailsDTO[]> casesResponse =
        Unirest.get(String.format("http://localhost:%d/cases/sampleunitids", port))
            .basicAuth("admin", "secret").queryString("sampleUnitId", sampleUnitId)
            .header("Content-Type", "application/json").asObject(CaseDetailsDTO[].class);

    UUID returnedCaseId = casesResponse.getBody()[0].getId();

    assertThat(returnedCaseId).isEqualTo(caseId);
  }

  /**
   * Test Collection Instrument downloaded case event works with B cases, and case group status has
   * transitioned to InProgress.
   *
   * @throws Exception
   */
  @Test
  public void testCreateCollectionInstrumentDownloadedCaseEventWithBCaseSuccess() throws Exception {

    // Given
    CaseNotification caseNotification = sendSampleUnit("BS12345", "B", UUID.randomUUID());

    String caseID = caseNotification.getCaseId();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO("TestEvent", CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            "SYSTEM", "DUMMY", UUID.randomUUID());

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret").header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO).asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID).basicAuth("admin", "secret")
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
   *
   * @throws Exception
   */
  @Test
  public void testCreateCollectionInstrumentErrorCaseEventWithBCaseSuccess() throws Exception {

    // Given
    CaseNotification caseNotification = sendSampleUnit("BS12345", "B", UUID.randomUUID());

    String caseID = caseNotification.getCaseId();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO("TestEvent", CategoryName.COLLECTION_INSTRUMENT_ERROR,
            "SYSTEM", "DUMMY", UUID.randomUUID());

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret").header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO).asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID).basicAuth("admin", "secret")
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
   *
   * @throws Exception
   */
  @Test
  public void testCreateSuccessfulResponseUploadCaseEventWithBCaseSuccess() throws Exception {

    // Given
    CaseNotification caseNotification = sendSampleUnit("BS12345", "B", UUID.randomUUID());

    String caseID = caseNotification.getCaseId();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO = new CaseEventCreationRequestDTO(
        "TestEvent", CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, "SYSTEM", "DUMMY", UUID.randomUUID());

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret").header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO).asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID).basicAuth("admin", "secret")
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
   *
   * @throws Exception
   */
  @Test
  public void testCreateUnsuccessfulResponseUploadCaseEventWithBCaseSuccess() throws Exception {

    // Given
    CaseNotification caseNotification = sendSampleUnit("BS12345", "B", UUID.randomUUID());

    String caseID = caseNotification.getCaseId();
    CaseEventCreationRequestDTO caseEventCreationRequestDTO =
        new CaseEventCreationRequestDTO("TestEvent", CategoryName.UNSUCCESSFUL_RESPONSE_UPLOAD,
            "SYSTEM", "DUMMY", UUID.randomUUID());

    // When
    HttpResponse<CreatedCaseEventDTO> createdCaseResponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/events")
            .basicAuth("admin", "secret").header("Content-Type", "application/json")
            .body(caseEventCreationRequestDTO).asObject(CreatedCaseEventDTO.class);

    HttpResponse<CaseDetailsDTO> returnedCaseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID).basicAuth("admin", "secret")
            .asObject(CaseDetailsDTO.class);

    CaseDetailsDTO affectedCase = returnedCaseResponse.getBody();

    // Then
    assertThat(createdCaseResponse.getStatus()).isEqualTo(201);
    assertThat(affectedCase.getCaseGroup().getCaseGroupStatus())
        .isNotEqualTo(CaseGroupStatus.COMPLETE);
  }

  /**
   * Creates a new SimpleMessageSender based on the config in AppConfig
   *
   * @return a new SimpleMessageSender
   */
  private SimpleMessageSender getMessageSender() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageSender(config.getHost(), config.getPort(), config.getUsername(),
        config.getPassword());
  }

  /**
   * Creates a new SimpleMessageListener based on the config in AppConfig
   *
   * @return a new SimpleMessageListener
   */
  private SimpleMessageListener getMessageListener() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageListener(config.getHost(), config.getPort(), config.getUsername(),
        config.getPassword());
  }

  private void createIACStub() throws IOException {
    this.wireMockRule.stubFor(post(urlPathEqualTo("/iacs")).willReturn(
        aResponse().withHeader("Content-Type", "application/json").withBody("[\"grtt7x2nhygg\"]")));
  }

  /**
   * Sends a sample unit in a message so that casesvc creates a case, then waits for a message on
   * the case lifecycle queue which confirms case creation
   *
   * @return a new CaseNotification
   */
  private CaseNotification sendSampleUnit(String sampleUnitRef, String sampleUnitType,
      UUID sampleUnitId) throws Exception {
    createIACStub();

    SimpleMessageSender sender = getMessageSender();

    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(UUID.randomUUID().toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef(sampleUnitRef);
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType(sampleUnitType);

    JAXBContext jaxbContext = JAXBContext.newInstance(SampleUnitParent.class);
    String xml = new Mapzer(resourceLoader).convertObjectToXml(jaxbContext, sampleUnit,
        "casesvc/xsd/inbound/SampleUnitNotification.xsd");

    sender.sendMessage("collection-inbound-exchange", "Case.CaseDelivery.binding", xml);

    String message = waitForNotification();

    jaxbContext = JAXBContext.newInstance(CaseNotification.class);
    return (CaseNotification) jaxbContext.createUnmarshaller()
        .unmarshal(new ByteArrayInputStream(message.getBytes()));
  }

  private String waitForNotification() throws Exception {

    SimpleMessageListener listener = getMessageListener();
    BlockingQueue<String> queue = listener.listen(SimpleMessageBase.ExchangeType.Direct,
        "case-outbound-exchange", "Case.LifecycleEvents.binding");

    String message = queue.take();
    log.info("message = " + message);
    assertNotNull("Timeout waiting for message to arrive in Case.LifecycleEvents", message);

    return message;
  }
}
