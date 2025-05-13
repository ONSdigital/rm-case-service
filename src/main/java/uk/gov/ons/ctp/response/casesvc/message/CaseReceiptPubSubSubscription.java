package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@Component
public class CaseReceiptPubSubSubscription {
  private static final Logger log = LoggerFactory.getLogger(CaseReceiptPubSubSubscription.class);
  @Autowired private AppConfig appConfig;
  @Autowired private CaseReceiptReceiver caseReceiptReceiver;
  @Autowired private PubSub pubSub;

  /**
   * Sets up a connection to the receipt subscription. A receipt is sent to us from SDX when they
   * receive and process a respondents EQ submission.
   *
   * @throws IOException
   */
  @EventListener(ApplicationReadyEvent.class)
  public void caseReceiptSubscription() throws IOException {
    ProjectSubscriptionName subscriptionName =
        ProjectSubscriptionName.of(
            appConfig.getGcp().getProject(), appConfig.getGcp().getReceiptSubscription());
    MessageReceiver receiver = createMessageReceiver();
    Subscriber subscriber = pubSub.getCaseReceiptSubscriber(receiver);
    subscriber.startAsync().awaitRunning();
    log.with("subscription", subscriptionName.toString())
        .info("Listening for messages on subscription");
  }

  public MessageReceiver createMessageReceiver() {
    return (PubsubMessage message, AckReplyConsumer consumer) -> {
      String payload = message.getData().toStringUtf8();
      log.with("payload", payload).info("Received a receipt");
      try {
        ObjectMapper mapper = new ObjectMapper();
        CaseReceipt receipt = mapper.readValue(payload, CaseReceipt.class);
        log.with("receipt", receipt).debug("Successfully serialised receipt");
        try {
          caseReceiptReceiver.process(receipt);
        } catch (CTPException e) {
          log.error(e, "Error processing receipt");
          consumer.nack();
        } catch (Exception e) {
          log.error(e, "Unexpected error processing receipt");
          consumer.nack();
        }
        consumer.ack();
      } catch (JsonProcessingException e) {
        log.error(e, "Error serialising receipt.");
        consumer.nack();
      }
    };
  }
}
