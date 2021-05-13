package uk.gov.ons.ctp.response.casesvc;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.lib.common.utility.Mapzer;
import uk.gov.ons.ctp.response.lib.rabbit.Rabbitmq;
import uk.gov.ons.ctp.response.lib.rabbit.SimpleMessageSender;

import javax.xml.bind.JAXBContext;
import java.util.UUID;

@Component
public class CaseCreator {

  private static final Logger log = LoggerFactory.getLogger(CaseCreator.class);

  @Autowired private AppConfig appConfig;
  @Autowired private ResourceLoader resourceLoader;
  @Autowired private IACServiceStub iacServiceStub;

  public void postSampleUnit(
      String sampleUnitRef, String sampleUnitType, UUID sampleUnitId, UUID collectionExerciseId)
      throws Exception {

    iacServiceStub.createIACStub();

    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(collectionExerciseId.toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActiveEnrolment(true);
    sampleUnit.setSampleUnitRef(sampleUnitRef);
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType(sampleUnitType);

    JAXBContext jaxbContext = JAXBContext.newInstance(SampleUnitParent.class);
    String xml =
        new Mapzer(resourceLoader)
            .convertObjectToXml(
                jaxbContext, sampleUnit, "casesvc/xsd/inbound/SampleUnitNotification.xsd");
    getMessageSender().sendMessage("collection-inbound-exchange", "Case.CaseDelivery.binding", xml);
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
}
