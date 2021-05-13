package uk.gov.ons.ctp.response.casesvc.message;

import com.google.api.gax.core.CredentialsProvider;
import com.google.api.gax.core.NoCredentialsProvider;
import com.google.api.gax.grpc.GrpcTransportChannel;
import com.google.api.gax.rpc.FixedTransportChannelProvider;
import com.google.api.gax.rpc.TransportChannelProvider;
import com.google.cloud.pubsub.v1.*;
import com.google.cloud.pubsub.v1.stub.GrpcSubscriberStub;
import com.google.cloud.pubsub.v1.stub.SubscriberStubSettings;
import com.google.pubsub.v1.*;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;

public class PubSubEmulator {
  private static final String HOST_PORT = "localhost:18681";
  public static final ManagedChannel CHANNEL =
      ManagedChannelBuilder.forTarget(HOST_PORT).usePlaintext().build();
  public static final TransportChannelProvider CHANNEL_PROVIDER =
      FixedTransportChannelProvider.create(GrpcTransportChannel.create(CHANNEL));
  public static final CredentialsProvider CREDENTIAL_PROVIDER = NoCredentialsProvider.create();
  private static final String PROJECT_ID = "test";
  private static final String TOPIC_ID = "test_topic";
  private static final String SUBSCRIPTION_ID = "test_subscription";
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
  ProjectSubscriptionName subscriptionName =
      ProjectSubscriptionName.of(PROJECT_ID, SUBSCRIPTION_ID);

  public PubSubEmulator() throws IOException {}

  public Publisher getEmulatorPublisher(TopicName topicName) throws IOException {
    return Publisher.newBuilder(topicName)
        .setChannelProvider(CHANNEL_PROVIDER)
        .setCredentialsProvider(CREDENTIAL_PROVIDER)
        .build();
  }

  public Subscriber getEmulatorSubscriber(MessageReceiver receiver) {
    return Subscriber.newBuilder(ProjectSubscriptionName.of("test", "test_subscription"), receiver)
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

  public void shutdown() {
    CHANNEL.shutdown();
  }

  public void testInit() throws IOException {
    Topic topic = topicClient.createTopic(topicName);
    System.out.println("Created topic: " + topic.getName());
    Subscription subscription =
        subscriptionAdminClient.createSubscription(
            subscriptionName, topicName, PushConfig.getDefaultInstance(), 10);
    System.out.println("Created pull subscription: " + subscription.getName());
  }

  public void testTeardown() {
    subscriptionAdminClient.deleteSubscription(subscriptionName);
    topicClient.deleteTopic(topicName);
  }
}
