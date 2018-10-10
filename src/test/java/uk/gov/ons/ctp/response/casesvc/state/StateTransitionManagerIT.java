package uk.gov.ons.ctp.response.casesvc.state;

import static com.github.tomakehurst.wiremock.client.WireMock.exactly;
import static com.github.tomakehurst.wiremock.client.WireMock.putRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.assertj.core.api.Java6Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import java.util.UUID;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.IACServiceStub;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseIACDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class StateTransitionManagerIT {

  @Rule
  public WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;
  @Autowired private IACServiceStub iacServiceStub;

  @BeforeClass
  public static void setUp() {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
  }

  @Test
  public void ensureSocialRefusalOutcomeMakesCaseInactionable() throws Exception {
    // Given
    String sampleUnitRef = "TEST1";
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit(sampleUnitRef, "H", UUID.randomUUID(), UUID.randomUUID());
    String collectionExerciseId = caseNotification.getExerciseId();
    String caseID = caseNotification.getCaseId();

    // When
    HttpResponse caseGroupTransitionResponse =
        Unirest.put(
                "http://localhost:"
                    + port
                    + "/casegroups/transitions/"
                    + collectionExerciseId
                    + "/"
                    + sampleUnitRef)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(
                String.format(
                    "{\"event\":\"%s\"}",
                    CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS))
            .asJson();
    assertThat(caseGroupTransitionResponse.getStatus()).isEqualTo(200);

    // Then
    HttpResponse<CaseDetailsDTO> caseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO.class);
    assertThat(caseResponse.getStatus()).isEqualTo(200);

    assertThat(caseResponse.getBody().getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.REFUSAL);
    assertThat(caseResponse.getBody().getState()).isEqualTo(CaseState.INACTIONABLE);
  }

  @Test
  public void ensureSocialOtherNonResponseOutcomeMakesCaseInactionable() throws Exception {
    // Given
    String sampleUnitRef = "TEST2";
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit(sampleUnitRef, "H", UUID.randomUUID(), UUID.randomUUID());
    String collectionExerciseId = caseNotification.getExerciseId();
    String caseID = caseNotification.getCaseId();

    // When
    HttpResponse caseGroupTransitionResponse =
        Unirest.put(
                "http://localhost:"
                    + port
                    + "/casegroups/transitions/"
                    + collectionExerciseId
                    + "/"
                    + sampleUnitRef)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(String.format("{\"event\":\"%s\"}", CategoryDTO.CategoryName.ILL_AT_HOME))
            .asJson();
    assertThat(caseGroupTransitionResponse.getStatus()).isEqualTo(200);

    // Then
    HttpResponse<CaseDetailsDTO> caseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO.class);
    assertThat(caseResponse.getStatus()).isEqualTo(200);

    assertThat(caseResponse.getBody().getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.OTHERNONRESPONSE);
    assertThat(caseResponse.getBody().getState()).isEqualTo(CaseState.INACTIONABLE);
  }

  @Test
  public void ensureSocialUnknownEligibilityOutcomeMakesCaseInactionable() throws Exception {
    // Given
    String sampleUnitRef = "TEST3";
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit(sampleUnitRef, "H", UUID.randomUUID(), UUID.randomUUID());
    String collectionExerciseId = caseNotification.getExerciseId();
    String caseID = caseNotification.getCaseId();

    // When
    HttpResponse caseGroupTransitionResponse =
        Unirest.put(
                "http://localhost:"
                    + port
                    + "/casegroups/transitions/"
                    + collectionExerciseId
                    + "/"
                    + sampleUnitRef)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(String.format("{\"event\":\"%s\"}", CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS))
            .asJson();
    assertThat(caseGroupTransitionResponse.getStatus()).isEqualTo(200);

    // Then
    HttpResponse<CaseDetailsDTO> caseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO.class);
    assertThat(caseResponse.getStatus()).isEqualTo(200);

    assertThat(caseResponse.getBody().getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.UNKNOWNELIGIBILITY);
    assertThat(caseResponse.getBody().getState()).isEqualTo(CaseState.INACTIONABLE);
  }

  @Test
  public void ensureSocialNotEligibleOutcomeMakesCaseInactionable() throws Exception {
    // Given
    String sampleUnitRef = "TEST4";
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit(sampleUnitRef, "H", UUID.randomUUID(), UUID.randomUUID());
    String collectionExerciseId = caseNotification.getExerciseId();
    String caseID = caseNotification.getCaseId();

    // When
    HttpResponse caseGroupTransitionResponse =
        Unirest.put(
                "http://localhost:"
                    + port
                    + "/casegroups/transitions/"
                    + collectionExerciseId
                    + "/"
                    + sampleUnitRef)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(String.format("{\"event\":\"%s\"}", CategoryDTO.CategoryName.VACANT_OR_EMPTY))
            .asJson();
    assertThat(caseGroupTransitionResponse.getStatus()).isEqualTo(200);

    // Then
    HttpResponse<CaseDetailsDTO> caseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO.class);
    assertThat(caseResponse.getStatus()).isEqualTo(200);

    assertThat(caseResponse.getBody().getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.NOTELIGIBLE);
    assertThat(caseResponse.getBody().getState()).isEqualTo(CaseState.INACTIONABLE);
  }

  @Test
  public void ensureSocialPartialInterviewRequestDeletionOutcomeDisablesUACs() throws Exception {
    // Given
    iacServiceStub.createIACStub();
    iacServiceStub.disableIACStub();

    String sampleUnitRef = "TEST";
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit(sampleUnitRef, "H", UUID.randomUUID(), UUID.randomUUID());

    String collectionExerciseId = caseNotification.getExerciseId();
    String caseID = caseNotification.getCaseId();

    // Create an additional UAC for the case to check it's disabled later
    HttpResponse<CaseIACDTO> createdUACresponse =
        Unirest.post("http://localhost:" + port + "/cases/" + caseID + "/iac")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseIACDTO.class);
    assertThat(createdUACresponse.getStatus()).isEqualTo(201);

    // Retrieve both IACs for our case
    HttpResponse<CaseIACDTO[]> caseIACs =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID + "/iac")
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseIACDTO[].class);
    assertThat(caseIACs.getStatus()).isEqualTo(200);

    // When
    // Make case 'INPROGRESS' to allow the state transition we're testing
    HttpResponse caseGroupTransitionEQlaunchResponse =
        Unirest.put(
                "http://localhost:"
                    + port
                    + "/casegroups/transitions/"
                    + collectionExerciseId
                    + "/"
                    + sampleUnitRef)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(String.format("{\"event\":\"%s\"}", CategoryDTO.CategoryName.EQ_LAUNCH))
            .asJson();
    assertThat(caseGroupTransitionEQlaunchResponse.getStatus()).isEqualTo(200);

    // Make the transition to something that will disable the IACs for the case
    HttpResponse caseGroupTransitionResponse =
        Unirest.put(
                "http://localhost:"
                    + port
                    + "/casegroups/transitions/"
                    + collectionExerciseId
                    + "/"
                    + sampleUnitRef)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .body(
                String.format(
                    "{\"event\":\"%s\"}",
                    CategoryDTO.CategoryName.PARTIAL_INTERVIEW_REQUEST_DATA_DELETED))
            .asJson();
    assertThat(caseGroupTransitionResponse.getStatus()).isEqualTo(200);

    // Then
    HttpResponse<CaseDetailsDTO> caseResponse =
        Unirest.get("http://localhost:" + port + "/cases/" + caseID)
            .basicAuth("admin", "secret")
            .header("Content-Type", "application/json")
            .asObject(CaseDetailsDTO.class);
    assertThat(caseResponse.getStatus()).isEqualTo(200);

    assertThat(caseResponse.getBody().getCaseGroup().getCaseGroupStatus())
        .isEqualTo(CaseGroupStatus.OTHERNONRESPONSE);
    assertThat(caseResponse.getBody().getState()).isEqualTo(CaseState.INACTIONABLE);
    verify(exactly(2), putRequestedFor(urlPathMatching("/iacs/[a-z0-9]*$")));
    verify(
        exactly(1),
        putRequestedFor(urlPathEqualTo(String.format("/iacs/%s", caseIACs.getBody()[0].getIac()))));
    verify(
        exactly(1),
        putRequestedFor(urlPathEqualTo(String.format("/iacs/%s", caseIACs.getBody()[1].getIac()))));
  }
}
