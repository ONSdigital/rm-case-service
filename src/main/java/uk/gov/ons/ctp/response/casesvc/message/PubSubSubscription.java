package uk.gov.ons.ctp.response.casesvc.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import com.google.pubsub.v1.PubsubMessage;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;

@Component
public class PubSubSubscription {
    private static final Logger log = LoggerFactory.getLogger(PubSubSubscription.class);
    private AppConfig appConfig;

    @EventListener(ApplicationReadyEvent.class)
    public void caseNotificationSubscription() {
        ProjectSubscriptionName subscriptionName =
                ProjectSubscriptionName.of("ras-rm-dev", appConfig.getGcp().getReceiptSubscription());
        // Instantiate an asynchronous message receiver.
        MessageReceiver receiver =
                (PubsubMessage message, AckReplyConsumer consumer) -> {
                    // Handle incoming message, then ack the received message.
                    log.info("Id: " + message.getMessageId());
                    log.info("Data: " + message.getData().toStringUtf8());
                    consumer.ack();
                };
        Subscriber subscriber = null;
        subscriber = Subscriber.newBuilder(subscriptionName, receiver).build();
        // Start the subscriber.
        subscriber.startAsync().awaitRunning();
        log.info("Listening for messages on [" + subscriptionName.toString() + "]");
    }
}
