package uk.gov.ons.ctp.response.casesvc.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.gax.core.ExecutorProvider;
import com.google.api.gax.core.InstantiatingExecutorProvider;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.ProjectSubscriptionName;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;

@Component
public class PubSub {
  private static final Logger log = LoggerFactory.getLogger(PubSub.class);
  @Autowired AppConfig appConfig;

  /**
   * * Provides subscription name for the case notification subscriber
   *
   * @return com.google.pubsub.v1.ProjectSubscriptionName
   */
  public ProjectSubscriptionName getCaseReceiptSubscriptionName() {
    String project = appConfig.getGcp().getProject();
    String subscriptionId = appConfig.getGcp().getReceiptSubscription();
    log.info(
        "creating pubsub subscription name for case creation notification "
            + subscriptionId
            + " in project "
            + project);
    return ProjectSubscriptionName.of(project, subscriptionId);
  }

  /**
   * Provides PubSub subscriber for case notification against message receiver
   *
   * @param receiver: com.google.cloud.pubsub.v1.MessageReceiver;
   * @return com.google.cloud.pubsub.v1.Subscriber;
   */
  public Subscriber getCaseReceiptSubscriber(MessageReceiver receiver) throws IOException {
    if (null == System.getenv("PUBSUB_EMULATOR_HOST")) {
      log.info("Returning Subscriber");
      ExecutorProvider executorProvider =
          InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(4).build();
      // `setParallelPullCount` determines how many StreamingPull streams the subscriber will open
      // to receive message. It defaults to 1. `setExecutorProvider` configures an executor for the
      // subscriber to process messages. Here, the subscriber is configured to open 2 streams for
      // receiving messages, each stream creates a new executor with 4 threads to help process the
      // message callbacks. In total 2x4=8 threads are used for message processing.
      return Subscriber.newBuilder(getCaseReceiptSubscriptionName(), receiver)
          .setParallelPullCount(2)
          .setExecutorProvider(executorProvider)
          .build();
    } else {
      log.info("Returning emulator Subscriber");
      return new PubSubEmulator().getEmulatorSubscriber(receiver);
    }
  }
}
