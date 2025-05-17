package uk.gov.ons.ctp.response.casesvc.message;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.pubsub.support.BasicAcknowledgeablePubsubMessage;
import com.google.cloud.spring.pubsub.support.GcpPubSubHeaders;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

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
    log.info("Receiving message ID from PubSub", kv("messageId", messageId));
    String payload = new String((byte[]) message.getPayload());
    log.info("New request for case notification", kv("payload", payload));
    try {
      log.info("Mapping payload to SampleUnitParent object", kv("messageId", messageId));
      SampleUnitParent caseCreation = objectMapper.readValue(payload, SampleUnitParent.class);
      log.info(
          "Mapping successful, case creation process initiated",
          kv("messageId", messageId),
          kv("sampleUnitRef", caseCreation.getSampleUnitRef()),
          kv("collectionExericseId", caseCreation.getCollectionExerciseId()));
      caseService.createInitialCase(caseCreation);
      log.info(
          "Case creation successful. Acking message",
          kv("messageId", messageId),
          kv("sampleUnitRef", caseCreation.getSampleUnitRef()),
          kv("collectionExericseId", caseCreation.getCollectionExerciseId()));
      pubSubMsg.ack();
    } catch (CTPException e) {
      if (e.getFault() == CTPException.Fault.DUPLICATE_RECORD) {
        log.info("Case already exists. Acking message", kv("payload", payload));
        pubSubMsg.ack();
      } else {
        pubSubMsg.nack();
      }
    } catch (final IOException e) {
      log.error(
          "Something went wrong while processing message received from PubSub "
              + "for case creation notification. Nacking message",
          kv("messageId", messageId),
          kv("exception", e));
      pubSubMsg.nack();
    } catch (Exception e) {
      log.error(
          "An unexpected exception occurred during the case creation. Nacking message",
          kv("messageId", messageId),
          kv("exception", e));
      pubSubMsg.nack();
    }
  }
}
