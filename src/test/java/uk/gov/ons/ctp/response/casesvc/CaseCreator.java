package uk.gov.ons.ctp.response.casesvc;

import static org.junit.Assert.assertNotNull;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.ByteArrayInputStream;
import java.util.UUID;
import java.util.concurrent.BlockingQueue;
import javax.xml.bind.JAXBContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.utility.Mapzer;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.tools.rabbit.Rabbitmq;
import uk.gov.ons.tools.rabbit.SimpleMessageBase;
import uk.gov.ons.tools.rabbit.SimpleMessageListener;
import uk.gov.ons.tools.rabbit.SimpleMessageSender;

@Component
public class CaseCreator {

  private static final Logger log = LoggerFactory.getLogger(CaseCreator.class);

  @Autowired private AppConfig appConfig;
  @Autowired private ResourceLoader resourceLoader;
  @Autowired private IACServiceStub iacServiceStub;

  /**
   * Sends a sample unit in a message so that casesvc creates a case, then waits for a message on
   * the case lifecycle queue which confirms case creation
   *
   * @return a new CaseNotification
   */
  public CaseNotification sendSampleUnit(
      String sampleUnitRef, String sampleUnitType, UUID sampleUnitId, UUID collectionExerciseId)
      throws Exception {

    iacServiceStub.createIACStub();

    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(collectionExerciseId.toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActionPlanId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitRef(sampleUnitRef);
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType(sampleUnitType);

    JAXBContext jaxbContext = JAXBContext.newInstance(SampleUnitParent.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(
                jaxbContext, sampleUnit, "casesvc/xsd/inbound/SampleUnitNotification.xsd");

    BlockingQueue<String> queue =
        getMessageListener()
            .listen(
                SimpleMessageBase.ExchangeType.Direct,
                "case-outbound-exchange",
                "Case.LifecycleEvents.binding");
    getMessageSender().sendMessage("collection-inbound-exchange", "Case.CaseDelivery.binding", xml);

    String message = waitForNotification(queue);

    jaxbContext = JAXBContext.newInstance(CaseNotification.class);
    return (CaseNotification)
        jaxbContext.createUnmarshaller().unmarshal(new ByteArrayInputStream(message.getBytes()));
  }

  private String waitForNotification(BlockingQueue<String> queue) throws Exception {
    String message = queue.take();
    log.info("message = " + message);
    assertNotNull("Timeout waiting for message to arrive in Case.LifecycleEvents", message);

    return message;
  }

  /**
   * Creates a new SimpleMessageSender based on the config in AppConfig
   *
   * @return a new SimpleMessageSender
   */
  private SimpleMessageSender getMessageSender() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageSender(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }

  /**
   * Creates a new SimpleMessageListener based on the config in AppConfig
   *
   * @return a new SimpleMessageListener
   */
  private SimpleMessageListener getMessageListener() {
    Rabbitmq config = this.appConfig.getRabbitmq();

    return new SimpleMessageListener(
        config.getHost(), config.getPort(), config.getUsername(), config.getPassword());
  }
}
