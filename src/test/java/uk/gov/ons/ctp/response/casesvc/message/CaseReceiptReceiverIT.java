package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.put;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.sun.org.apache.xerces.internal.jaxp.datatype.XMLGregorianCalendarImpl;
import java.util.GregorianCalendar;
import java.util.UUID;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.common.UnirestInitialiser;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventCreationRequestDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CreatedCaseEventDTO;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseReceiptReceiverIT {

  @Rule
  public WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @Autowired private MessageChannel caseReceiptTransformed;

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;

  @BeforeClass
  public static void setUp() {
    ObjectMapper value = new ObjectMapper();
    UnirestInitialiser.initialise(value);
  }

  @Test
  public void socialCaseShouldReceipt() throws Exception {
    // Given
    UUID sampleUnitId = UUID.randomUUID();
    CaseNotification caseNotif = caseCreator.sendSampleUnit("LMS0003", "H", sampleUnitId);
    startCase(caseNotif.getCaseId());
    XMLGregorianCalendarImpl now = new XMLGregorianCalendarImpl(new GregorianCalendar());
    CaseReceipt caseReceipt =
        new CaseReceipt("caseRef", caseNotif.getCaseId(), InboundChannel.OFFLINE, now);
    Message<CaseReceipt> message = new GenericMessage<>(caseReceipt);
    disableIACStub();

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

  private void disableIACStub() {
    stubFor(
        put(urlPathMatching("/iacs/(.*)"))
            .willReturn(
                aResponse().withHeader("Content-Type", "application/json").withStatus(200)));
  }
}
