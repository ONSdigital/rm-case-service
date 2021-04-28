package uk.gov.ons.ctp.response.casesvc.message;

// Define what happens to the messages arriving in the message channel.

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.core.PubSubTemplate;
import org.springframework.cloud.gcp.pubsub.integration.AckMode;
import org.springframework.cloud.gcp.pubsub.integration.inbound.PubSubInboundChannelAdapter;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Qualifier;

import org.springframework.messaging.MessageChannel;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;


public class PubSubCaseReceiptSubscriptionAdapter {
    private static final Logger log = LoggerFactory.getLogger(PubSubCaseReceiptSubscriptionAdapter.class);

    @Autowired AppConfig appConfig;

    @Bean
    public PubSubInboundChannelAdapter inboundChannelAdapter(
            @Qualifier("inputMessageChannel") MessageChannel messageChannel,
            PubSubTemplate pubSubTemplate) {
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, appConfig.getGcp().getReceiptSubscription());
        adapter.setOutputChannel(messageChannel);
        adapter.setAckMode(AckMode.MANUAL);
        adapter.setPayloadType(String.class);
        return adapter;
    }
}

