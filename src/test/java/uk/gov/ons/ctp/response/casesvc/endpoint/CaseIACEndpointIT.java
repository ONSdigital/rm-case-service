package uk.gov.ons.ctp.response.casesvc.endpoint;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.util.UUID;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.endpoint.CaseIACEndpoint.CaseIACDTO;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseIACEndpointIT {

  private static final Logger log = LoggerFactory.getLogger(CaseIACEndpointIT.class);

  @Rule
  public WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;

  @BeforeClass
  public static void setUp() {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
  }

  @Test
  public void shouldCreateNewIACCode() throws Exception {

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS12345", "B", UUID.randomUUID());
    String notExpected = getCurrentIACCode(caseNotification.getCaseId());

    // When
    HttpResponse<String> actual = generateNewIACCode(caseNotification.getCaseId());

    // Then
    assertThat(actual.getStatus(), is(equalTo(HttpStatus.CREATED.value()))); // assert IAC in model
    assertThat(actual.getBody(), not(equalTo(notExpected)));
  }

  @Test
  public void shouldGetIacCodes() throws Exception {

    // Given
    CaseNotification caseNotification =
        caseCreator.sendSampleUnit("BS123456", "B", UUID.randomUUID());

    // When
    HttpResponse<CaseIACDTO[]> iacs =
        Unirest.get("http://localhost:{port}/cases/{caseId}/iac")
            .routeParam("port", Integer.toString(port))
            .routeParam("caseId", caseNotification.getCaseId())
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
}
