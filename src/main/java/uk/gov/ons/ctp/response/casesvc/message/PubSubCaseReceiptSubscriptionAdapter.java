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
    public PubSubInboundChannelAdapter messageChannelAdapter(
            @Qualifier("pubsubInputChannel") MessageChannel inputChannel,
            PubSubTemplate pubSubTemplate) {
        log.info("Receipt subscription [" + appConfig.getGcp().getReceiptSubscription() + "]");
        PubSubInboundChannelAdapter adapter =
                new PubSubInboundChannelAdapter(pubSubTemplate, appConfig.getGcp().getReceiptSubscription());
        adapter.setOutputChannel(inputChannel);
        adapter.setAckMode(AckMode.MANUAL);

        return adapter;
    }
}

