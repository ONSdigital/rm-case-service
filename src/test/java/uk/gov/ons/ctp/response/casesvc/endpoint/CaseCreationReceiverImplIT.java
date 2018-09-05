package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.transformer.MessageTransformationException;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandlingException;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

import java.util.UUID;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class CaseCreationReceiverImplIT {

  @LocalServerPort private int port;

  @Autowired private CaseCreator caseCreator;

  @Autowired private MessageChannel caseTransformed;

  @Rule
  public WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @MockBean private CaseService caseService;

  @Test(expected = MessageHandlingException.class)
  public void ensureFiniteRetriesOnFailedCaseNotification() throws Exception {
    doThrow(RuntimeException.class).when(caseService).createInitialCase(any());

    UUID sampleUnitId = UUID.randomUUID();
    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(UUID.randomUUID().toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef("LMS0004");
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType("H");
    Message<SampleUnitParent> caseMessage = new GenericMessage<>(sampleUnit);
    caseTransformed.send(caseMessage);

    verify(caseService, times(3)).createInitialCase(any());
  }
}
