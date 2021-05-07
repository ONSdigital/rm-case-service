package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.feedback.CaseReceipt;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

@Component
public class PubSubSubscription {
    private static final Logger log = LoggerFactory.getLogger(PubSubSubscription.class);
    @Autowired private AppConfig appConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void caseNotificationSubscription() {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of("ras-rm-dev", appConfig.getGcp().getReceiptSubscription());
        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    String payload = message.getData().toStringUtf8();
                    log.info("Data: " + payload);
                    ObjectMapper mapper = new ObjectMapper();
                    CaseReceipt receipt = null;
                    try {
                        receipt = mapper.readValue(payload, CaseReceipt.class);
                    } catch (JsonProcessingException e) {
                        log.error(String.valueOf(e));
                        throw new RuntimeException(e);
                    }
                    CaseReceiptReceiver caseReceiptReceiver = new CaseReceiptReceiver();
                    try {
                        caseReceiptReceiver.process(receipt);
                    } catch (CTPException e) {
                        log.error(String.valueOf(e));
                        throw new RuntimeException(e);
                    }
                    consumer.ack();
                };
        Subscriber subscriber = null;
        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        // Start the subscriber.
        subscriber.startAsync().awaitRunning();
        log.info("Listening for messages on [" + subscriptionName.toString() + "]");
    }
}
