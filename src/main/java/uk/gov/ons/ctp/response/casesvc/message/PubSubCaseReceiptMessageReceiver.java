package uk.gov.ons.ctp.response.casesvc.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.messaging.handler.annotation.Header;

public class PubSubCaseReceiptMessageReceiver {
    private static final Logger log = LoggerFactory.getLogger(PubSubCaseReceiptMessageReceiver.class);

    // Define what happens to the messages arriving in the message channel.
    @ServiceActivator(inputChannel = "inputMessageChannel")
    public void messageReceiver(
            String payload,
            @Header(GcpPubSubHeaders.ORIGINAL_MESSAGE) BasicAcknowledgeablePubsubMessage message) {
        log.info("Message arrived via an inbound channel adapter from sub-one! Payload: " + payload);
        message.ack();
    }
}
