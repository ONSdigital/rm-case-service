package uk.gov.ons.ctp.response.casesvc.message;

import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static junit.framework.TestCase.assertNotNull;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.github.tomakehurst.wiremock.extension.responsetemplating.ResponseTemplateTransformer;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import java.io.ByteArrayInputStream;
import java.sql.Time;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.datatype.XMLGregorianCalendar;
import org.aopalliance.aop.Advice;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ResourceLoader;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.GenericMessage;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import uk.gov.ons.ctp.common.time.DateTimeUtil;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.casesvc.CaseCreator;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.tools.rabbit.SimpleMessageBase.ExchangeType;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

@ContextConfiguration
@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@RunWith(SpringJUnit4ClassRunner.class)
public class RabbitSpringIntegrationDLQAndRetriesIT {

  @LocalServerPort private int port;

  @Autowired
  AppConfig appConfig;

  @Autowired private ResourceLoader resourceLoader;

  @Rule
  public WireMockRule wireMockRule =
      new WireMockRule(options().extensions(new ResponseTemplateTransformer(false)).port(18002));

  @MockBean private CaseService caseService;

  private SimpleMessageListener listener;
  private SimpleMessageSender sender;

  @Before
  public void setup() {
    sender = new SimpleMessageSender(
            appConfig.getRabbitmq().getHost(), appConfig.getRabbitmq().getPort(),
            appConfig.getRabbitmq().getUsername(), appConfig.getRabbitmq().getPassword());

    listener = new SimpleMessageListener(
            appConfig.getRabbitmq().getHost(), appConfig.getRabbitmq().getPort(),
            appConfig.getRabbitmq().getUsername(), appConfig.getRabbitmq().getPassword());
  }

  @Test
  public void ensureFiniteRetriesOnFailedCaseNotification() throws Exception {
    doThrow(new RuntimeException()).when(caseService).createInitialCase(any());

    UUID sampleUnitId = UUID.randomUUID();
    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(UUID.randomUUID().toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef("LMS0004");
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType("H");

    JAXBContext jaxbContext = JAXBContext.newInstance(SampleUnitParent.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(
                jaxbContext, sampleUnit, "casesvc/xsd/inbound/SampleUnitNotification.xsd");


    sender.sendMessageToQueue("Case.CaseDelivery", xml);
    String message = listener.listen(ExchangeType.Direct,
        "case-deadletter-exchange", "Case.CaseDelivery.binding").take();

    SampleUnitParent received = (SampleUnitParent) jaxbContext
            .createUnmarshaller()
            .unmarshal(new ByteArrayInputStream(message.getBytes()));

    assertEquals(received.getId(), sampleUnit.getId());

    verify(caseService, times(3)).createInitialCase(any());
  }

  @Test
  public void ensureFiniteRetriesOnFailedCaseReceipt() throws Exception {
    doThrow(new RuntimeException()).when(caseService).findCaseById(any());

    UUID caseId = UUID.randomUUID();
    CaseReceipt caseReceipt = new CaseReceipt();
    caseReceipt.setCaseId(caseId.toString());
    caseReceipt.setInboundChannel(InboundChannel.PAPER);
    caseReceipt.setResponseDateTime(DateTimeUtil.giveMeCalendarForNow());
    caseReceipt.setCaseRef("NOODLE");

    JAXBContext jaxbContext = JAXBContext.newInstance(CaseReceipt.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(
                jaxbContext, caseReceipt, "casesvc/xsd/inbound/caseReceipt.xsd");


    sender.sendMessageToQueue("Case.Responses", xml);
    String message = listener.listen(ExchangeType.Direct,
        "case-deadletter-exchange", "Case.Responses.binding").poll(
        30, TimeUnit.SECONDS);

    CaseReceipt dlqCaseReceipt = (CaseReceipt) jaxbContext
        .createUnmarshaller()
        .unmarshal(new ByteArrayInputStream(message.getBytes()));

    assertEquals(caseReceipt.getCaseId(), dlqCaseReceipt.getCaseId());

    verify(caseService, times(3)).findCaseById(any());
  }
}
