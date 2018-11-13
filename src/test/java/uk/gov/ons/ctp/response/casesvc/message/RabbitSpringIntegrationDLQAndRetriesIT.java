package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static org.junit.Assert.assertEquals;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.ByteArrayInputStream;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBContext;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ResourceLoader;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;
import uk.gov.ons.tools.rabbit.SimpleMessageBase.ExchangeType;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
public class RabbitSpringIntegrationDLQAndRetriesIT {

  @LocalServerPort private int port;

  @ClassRule
  public static WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @Autowired AppConfig appConfig;

  @Autowired private ResourceLoader resourceLoader;
  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;

  private SimpleMessageListener listener;
  private SimpleMessageSender sender;

  @Before
  public void setup() {
    sender =
        new SimpleMessageSender(
            appConfig.getRabbitmq().getHost(), appConfig.getRabbitmq().getPort(),
            appConfig.getRabbitmq().getUsername(), appConfig.getRabbitmq().getPassword());

    listener =
        new SimpleMessageListener(
            appConfig.getRabbitmq().getHost(), appConfig.getRabbitmq().getPort(),
            appConfig.getRabbitmq().getUsername(), appConfig.getRabbitmq().getPassword());
  }

  @Test
  public void ensureFailedCaseNotificationIsDeadLetterQueuedAfterFiniteRetries() throws Exception {
    Random rnd = new Random();

    int randNumber = 10000 + rnd.nextInt(900000);

    UUID surveyId = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");

    collectionExerciseSvcClient.createCollectionExercise(
        surveyId, Integer.toString(randNumber), "January 2018");

    CollectionExerciseDTO collex =
        collectionExerciseSvcClient.getCollectionExercises(surveyId.toString()).get(0);

    UUID sampleUnitId = UUID.randomUUID();
    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(collex.getId().toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef("LMS0004");
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType("VALUE_WHICH_WILL_SOME_SORT_OF_EXCEPTION_BECAUSE_ITS_GARBAGE");

    JAXBContext jaxbContext = JAXBContext.newInstance(SampleUnitParent.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(
                jaxbContext, sampleUnit, "casesvc/xsd/inbound/SampleUnitNotification.xsd");

    sender.sendMessageToQueue("Case.CaseDelivery", xml);
    String message =
        listener
            .listen(ExchangeType.Direct, "case-deadletter-exchange", "Case.CaseDelivery.binding")
            .poll(30, TimeUnit.SECONDS);

    SampleUnitParent received =
        (SampleUnitParent)
            jaxbContext
                .createUnmarshaller()
                .unmarshal(new ByteArrayInputStream(message.getBytes()));

    assertEquals(received.getId(), sampleUnit.getId());
  }

  @Test
  public void ensureFailedCaseReceiptIsDeadLetterQueuedAfterFiniteRetries() throws Exception {
    String caseId = "VALUE_WHICH_WILL_SOME_SORT_OF_EXCEPTION_BECAUSE_ITS_GARBAGE";
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseId(caseId);
    caseReceipt.setInboundChannel(InboundChannel.PAPER);
    caseReceipt.setResponseDateTime(DateTimeUtil.giveMeCalendarForNow());
    caseReceipt.setCaseRef("TESTCASEREF");

    JAXBContext jaxbContext = JAXBContext.newInstance(CaseReceipt.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(jaxbContext, caseReceipt, "casesvc/xsd/inbound/caseReceipt.xsd");

    sender.sendMessageToQueue("Case.Responses", xml);
    String message =
        listener
            .listen(ExchangeType.Direct, "case-deadletter-exchange", "Case.Responses.binding")
            .poll(30, TimeUnit.SECONDS);

    CaseReceipt dlqCaseReceipt =
        (CaseReceipt)
            jaxbContext
                .createUnmarshaller()
                .unmarshal(new ByteArrayInputStream(message.getBytes()));

    assertEquals(caseReceipt.getCaseId(), dlqCaseReceipt.getCaseId());
  }
}
