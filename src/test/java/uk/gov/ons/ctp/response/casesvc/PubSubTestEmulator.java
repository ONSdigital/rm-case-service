package uk.gov.ons.ctp.response.casesvc;

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

/** * This is a PubSub Emulator class which is used for testing pubsub function */
public class PubSubTestEmulator {
  private static final Logger log = LoggerFactory.getLogger(PubSubTestEmulator.class);
  private static final String HOST_PORT = "localhost:18681";
  public static final ManagedChannel CHANNEL =
      ManagedChannelBuilder.forTarget(HOST_PORT).usePlaintext().build();
  public static final TransportChannelProvider CHANNEL_PROVIDER =
      FixedTransportChannelProvider.create(GrpcTransportChannel.create(CHANNEL));
  public static final CredentialsProvider CREDENTIAL_PROVIDER = NoCredentialsProvider.create();
  private static final String PROJECT_ID = "ras-rm-dev";
  private static final String RECEIPT_TOPIC_ID = "test_receipt_topic";
  private static final String CASE_CREATION_TOPIC_ID = "test_case_creation_topic";
  private static final String RECEIPT_SUBSCRIPTION_ID = "test_receipt_subscription";
  private static final String CASE_CREATION_SUBSCRIPTION_ID = "test_case_creation_subscription";
  private final TopicAdminClient topicClient =
      TopicAdminClient.create(
          TopicAdminSettings.newBuilder()
              .setTransportChannelProvider(PubSubTestEmulator.CHANNEL_PROVIDER)
              .setCredentialsProvider(PubSubTestEmulator.CREDENTIAL_PROVIDER)
              .build());
  SubscriptionAdminClient subscriptionAdminClient =
      SubscriptionAdminClient.create(
          SubscriptionAdminSettings.newBuilder()
              .setTransportChannelProvider(PubSubTestEmulator.CHANNEL_PROVIDER)
              .setCredentialsProvider(PubSubTestEmulator.CREDENTIAL_PROVIDER)
              .build());
  TopicName topicName = TopicName.of(PROJECT_ID, RECEIPT_TOPIC_ID);
  TopicName caseCreationTopicName = TopicName.of(PROJECT_ID, CASE_CREATION_TOPIC_ID);
  ProjectSubscriptionName subscriptionName =
      ProjectSubscriptionName.of(PROJECT_ID, RECEIPT_SUBSCRIPTION_ID);
  ProjectSubscriptionName caseCreationSubscriptionName =
      ProjectSubscriptionName.of(PROJECT_ID, CASE_CREATION_SUBSCRIPTION_ID);

  public PubSubTestEmulator() throws IOException {}

  public Publisher getEmulatorPublisher(TopicName topicName) throws IOException {
    return Publisher.newBuilder(topicName)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public Subscriber getEmulatorSubscriber(MessageReceiver receiver, String subscription) {
    return Subscriber.newBuilder(ProjectSubscriptionName.of(PROJECT_ID, subscription), receiver)
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
      TopicName topicName = TopicName.of(PROJECT_ID, RECEIPT_TOPIC_ID);
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
      log.error("Failed to publish message", e);
    }
  }

  public void shutdown() {
    CHANNEL.shutdown();
  }

  public void testInit() {
    Topic topic = topicClient.createTopic(topicName);
    log.info("Created topic: " + topic.getName());
    Subscription subscription =
        subscriptionAdminClient.createSubscription(
            subscriptionName, topicName, PushConfig.getDefaultInstance(), 10);
    log.info("Created pull subscription: " + subscription.getName());
  }

  public void testTeardown() {
    subscriptionAdminClient.deleteSubscription(subscriptionName);
    topicClient.deleteTopic(topicName);
  }
}
