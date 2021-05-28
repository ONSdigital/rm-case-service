package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/** Receive a new case from the Collection Exercise service. */
@Component
public class CaseCreationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseCreationReceiver.class);

  @Autowired private PubSub pubSub;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private CaseService caseService;

  /**
   * To process SampleUnitParents read from pubsub This creates application ready event listener to
   * provide a active subscription for the new case creation against the receiving SampleUnitParents
   */
  @EventListener(ApplicationReadyEvent.class)
  public void acceptSampleUnit() throws IOException {
    log.debug("received CaseCreation Message from pubsub");
    // Instantiate an asynchronous message receiver.
    MessageReceiver receiver =
        (PubsubMessage message, AckReplyConsumer consumer) -> {
          // Handle incoming message, then ack the received message.
          log.with(message.getMessageId()).info("Receiving message ID from PubSub");
          log.with(message.getData().toString()).debug("Receiving data from PubSub ");
          try {
            SampleUnitParent caseCreation =
                objectMapper.readValue(message.getData().toStringUtf8(), SampleUnitParent.class);
            caseService.createInitialCase(caseCreation);
            consumer.ack();
          } catch (final IOException e) {
            log.with(e)
                .error(
                    "Something went wrong while processing message received from PubSub "
                        + "for case creation notification");
            consumer.nack();
          }
        };
    Subscriber subscriber = pubSub.getCaseCreationNotificationSubscriber(receiver);
    // Start the subscriber.
    subscriber.startAsync().awaitRunning();
    log.with(subscriber.getSubscriptionNameString())
        .info("Listening for case creation notification messages on PubSub-subscription id");
  }
}
