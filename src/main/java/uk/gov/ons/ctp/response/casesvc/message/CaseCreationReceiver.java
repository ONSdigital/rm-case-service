package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/** Receive a new case from the Collection Exercise service. */
@Component
public class CaseCreationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseCreationReceiver.class);
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CaseService caseService;
  @Autowired AppConfig appConfig;

  /**
   * To process SampleUnitParents read from pubsub This creates application ready event listener to
   * provide a active subscription for the new case creation against the receiving SampleUnitParents
   */
  @ServiceActivator(inputChannel = "caseCreationChannel")
  public void messageReceiver(
      Message message,
      @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage pubSubMsg) {
    String messageId = pubSubMsg.getPubsubMessage().getMessageId();
    log.with("messageId", messageId).info("Receiving message ID from PubSub");
    String payload = new String((byte[]) message.getPayload());
    log.with("payload", payload).info("New request for case notification");
    try {
      log.with("messageId", messageId).info("Mapping payload to SampleUnitParent object");
      SampleUnitParent caseCreation = objectMapper.readValue(payload, SampleUnitParent.class);
      log.with("messageId", messageId)
          .with("sampleUnitRef", caseCreation.getSampleUnitRef())
          .with("collectionExericseId", caseCreation.getCollectionExerciseId())
          .info("Mapping successful, case creation process initiated");
      caseService.createInitialCase(caseCreation);
      log.with("messageId", messageId)
          .with("sampleUnitRef", caseCreation.getSampleUnitRef())
          .with("collectionExericseId", caseCreation.getCollectionExerciseId())
          .info("Case creation successful. Acking message");
      pubSubMsg.ack();
    } catch (final IOException e) {
      log.with("messageId", messageId)
          .with(e)
          .error(
              "Something went wrong while processing message received from PubSub "
                  + "for case creation notification. Nacking message");
      pubSubMsg.nack();
    } catch (Exception e) {
      log.with("messageId", messageId)
          .with(e)
          .error("An unexpected exception occurred during the case creation. Nacking message");
      pubSubMsg.nack();
    }
  }
}
