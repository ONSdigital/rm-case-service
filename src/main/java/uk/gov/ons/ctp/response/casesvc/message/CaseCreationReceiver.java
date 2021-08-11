package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gcp.pubsub.support.BasicAcknowledgeablePubsubMessage;
import org.springframework.cloud.gcp.pubsub.support.GcpPubSubHeaders;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

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
  //  @EventListener(ApplicationReadyEvent.class)
  //  public void acceptSampleUnit() throws IOException {
  //    log.debug("received CaseCreation Message from pubsub");
  //    // Instantiate an asynchronous message receiver.
  //    MessageReceiver receiver =
  //        (PubsubMessage message, AckReplyConsumer consumer) -> {
  //          // Handle incoming message, then ack the received message.
  //          log.with(message.getMessageId()).info("Receiving message ID from PubSub");
  //          log.with(message.getData().toString()).debug("Receiving data from PubSub ");
  //          try {
  //            SampleUnitParent caseCreation =
  //                objectMapper.readValue(message.getData().toStringUtf8(),
  // SampleUnitParent.class);
  //            caseService.createInitialCase(caseCreation);
  //            consumer.ack();
  //          } catch (final IOException e) {
  //            log.with(e)
  //                .error(
  //                    "Something went wrong while processing message received from PubSub "
  //                        + "for case creation notification");
  //            consumer.nack();
  //          }
  //        };
  //    Subscriber subscriber = getCaseCreationNotificationSubscriber(receiver);
  //    // Start the subscriber.
  //    subscriber.startAsync().awaitRunning();
  //    log.with(subscriber.getSubscriptionNameString())
  //        .info("Listening for case creation notification messages on PubSub-subscription id");
  //  }
  //
  //  /**
  //   * Provides PubSub subscriber for case creation notification against message receiver
  //   *
  //   * @param receiver: com.google.cloud.pubsub.v1.MessageReceiver;
  //   * @return com.google.cloud.pubsub.v1.Subscriber;
  //   */
  //  private Subscriber getCaseCreationNotificationSubscriber(MessageReceiver receiver)
  //      throws IOException {
  //    if (StringUtil.isBlank(System.getenv("PUBSUB_EMULATOR_HOST"))) {
  //      log.info("Returning Subscriber for case creation notification");
  //      ExecutorProvider executorProvider =
  //          InstantiatingExecutorProvider.newBuilder().setExecutorThreadCount(4).build();
  //      // `setParallelPullCount` determines how many StreamingPull streams the subscriber will
  // open
  //      // to receive message. It defaults to 1. `setExecutorProvider` configures an executor for
  // the
  //      // subscriber to process messages. Here, the subscriber is configured to open 2 streams
  // for
  //      // receiving messages, each stream creates a new executor with 4 threads to help process
  // the
  //      // message callbacks. In total 2x4=8 threads are used for message processing.
  //      return Subscriber.newBuilder(getCaseCreationSubscriptionName(), receiver)
  //          .setParallelPullCount(2)
  //          .setExecutorProvider(executorProvider)
  //          .build();
  //    } else {
  //      log.info("Returning emulator Subscriber");
  //      return new PubSubEmulator().getEmulatorSubscriberForCaseCreationNotification(receiver);
  //    }
  //  }
  //
  //  /***
  //   * Provides subscription name for the case notification subscriber
  //   * @return com.google.pubsub.v1.ProjectSubscriptionName
  //   */
  //  private ProjectSubscriptionName getCaseCreationSubscriptionName() {
  //    String project = appConfig.getGcp().getProject();
  //    String subscriptionId = appConfig.getGcp().getCaseNotificationSubscription();
  //    log.with("Subscription id", subscriptionId)
  //        .with("project", project)
  //        .info("creating pubsub subscription name for case notification ");
  //    return ProjectSubscriptionName.of(project, subscriptionId);
  //  }

  @ServiceActivator(inputChannel = "caseCreationChannel")
  public void messageReceiver(Message message) {
    String payload = new String((byte[]) message.getPayload());
    log.info("Message arrived! Payload: " + payload);
    BasicAcknowledgeablePubsubMessage originalMessage =
        message
            .getHeaders()
            .get(GcpPubSubHeaders.ORIGINAL_MESSAGE, BasicAcknowledgeablePubsubMessage.class);
    try {
      SampleUnitParent caseCreation = objectMapper.readValue(payload, SampleUnitParent.class);
      caseService.createInitialCase(caseCreation);
    } catch (final IOException e) {
      log.with(e)
          .error(
              "Something went wrong while processing message received from PubSub "
                  + "for case creation notification");
    }
  }
}
