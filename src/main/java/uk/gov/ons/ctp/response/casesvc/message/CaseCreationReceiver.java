package uk.gov.ons.ctp.response.casesvc.message;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
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
      @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage pubSubMsg)
      throws CTPException {
    log.info(
        "Receiving message ID from PubSub",
        kv("messageId", pubSubMsg.getPubsubMessage().getMessageId()));
    String payload = new String((byte[]) message.getPayload());
    log.info("New request for case notification", kv("payload", payload));
    try {
      log.info("Mapping payload to SampleUnitParent object");
      SampleUnitParent caseCreation = objectMapper.readValue(payload, SampleUnitParent.class);
      log.info("Mapping successful, case creation process initiated");
      caseService.createInitialCase(caseCreation);
      pubSubMsg.ack();
    } catch (final IOException e) {
      log.with(e)
          .error(
              "Something went wrong while processing message received from PubSub "
                  + "for case creation notification");
      pubSubMsg.nack();
    }
  }
}
