package uk.gov.ons.ctp.response.casesvc.utility;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.google.api.core.ApiFuture;
import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * * This is a PubSub Emulator class. This is a utility class which is used for testing pubsub
 * function
 */
public class PubSubEmulator {
  private static final Logger log = LoggerFactory.getLogger(PubSubEmulator.class);
  private static final String HOST_PORT = "pubsub-emulator:8681";
  // private static final String HOST_PORT = "localhost:18681";
  public static final ManagedChannel CHANNEL =
      ManagedChannelBuilder.forTarget(HOST_PORT).usePlaintext().build();
  public static final TransportChannelProvider CHANNEL_PROVIDER =
      FixedTransportChannelProvider.create(GrpcTransportChannel.create(CHANNEL));
  public static final CredentialsProvider CREDENTIAL_PROVIDER = NoCredentialsProvider.create();
  private static final String PROJECT_ID = "local";
  private static final String TOPIC_ID = "ras-rm-notify-local";
  private static final String CASE_CREATION_TOPIC_ID = "case-notification-local";
  private static final String SUBSCRIPTION_ID = "sdx-receipt-local";
  private static final String CASE_CREATION_SUBSCRIPTION_ID = "case-notification-local";
  private final TopicAdminClient topicClient =
      TopicAdminClient.create(
          TopicAdminSettings.newBuilder()
              .setTransportChannelProvider(PubSubEmulator.CHANNEL_PROVIDER)
              .setCredentialsProvider(PubSubEmulator.CREDENTIAL_PROVIDER)
              .build());
  SubscriptionAdminClient subscriptionAdminClient =
      SubscriptionAdminClient.create(
          SubscriptionAdminSettings.newBuilder()
              .setTransportChannelProvider(PubSubEmulator.CHANNEL_PROVIDER)
              .setCredentialsProvider(PubSubEmulator.CREDENTIAL_PROVIDER)
              .build());
  TopicName topicName = TopicName.of(PROJECT_ID, TOPIC_ID);
  TopicName caseCreationTopicName = TopicName.of(PROJECT_ID, CASE_CREATION_TOPIC_ID);
  ProjectSubscriptionName subscriptionName =
      ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID);
  ProjectSubscriptionName caseCreationSubscriptionName =
      ProjectSubscriptionName.of(PROJECT_ID, CASE_CREATION_SUBSCRIPTION_ID);

  public PubSubEmulator() throws IOException {}

  public Publisher getEmulatorPublisher(TopicName topicName) throws IOException {
    return Publisher.newBuilder(topicName)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public Subscriber getEmulatorSubscriber(MessageReceiver receiver) {
    return Subscriber.newBuilder(ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID), receiver)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public Subscriber getEmulatorSubscriberForCaseActionEventStatus(MessageReceiver receiver) {
    return Subscriber.newBuilder(
            ProjectSubscriptionName.of(PROJECT_ID, "test_event_status_subscription"), receiver)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public GrpcSubscriberStub getEmulatorSubscriberStub() throws IOException {
    return GrpcSubscriberStub.create(
        SubscriberStubSettings.newBuilder()
            .setTransportChannelProvider(CHANNEL_PROVIDER)
            .setCredentialsProvider(CREDENTIAL_PROVIDER)
            .build());
  }

  public void publishMessage(String message) {
    try {
      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
      TopicName topicName = TopicName.of(PROJECT_ID, TOPIC_ID);
      Publisher publisher = getEmulatorPublisher(topicName);
      log.info("Publishing message to pubsub emulator", kv("publisher", publisher));
      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      String messageId = messageIdFuture.get();
      log.info("Published message to pubsub emulator", kv("messageId", messageId));
    } catch (IOException | InterruptedException | ExecutionException e) {
      log.error("Failed to publish message", e);
    }
  }

  public void publishSampleCaseCreationMessage(String message) {
    try {
      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
      TopicName topicName = TopicName.of(PROJECT_ID, CASE_CREATION_TOPIC_ID);
      Publisher publisher = getEmulatorPublisher(topicName);
      log.info("Publishing case creation message to pubsub emulator", kv("publisher", publisher));
      ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
      String messageId = messageIdFuture.get();
      log.info("Published case creation message to pubsub emulator", kv("messageId", messageId));
    } catch (IOException | InterruptedException | ExecutionException e) {
      log.error("Failed to publish message", kv("exception", e));
    }
  }

  public void shutdown() {
    CHANNEL.shutdown();
  }
}
