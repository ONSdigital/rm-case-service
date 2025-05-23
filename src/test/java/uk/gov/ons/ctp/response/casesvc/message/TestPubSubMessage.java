package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.pubsub.v1.AckReplyConsumer;
import com.google.cloud.pubsub.v1.MessageReceiver;
import com.google.cloud.pubsub.v1.Subscriber;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionEvent;
import uk.gov.ons.ctp.response.casesvc.utility.PubSubEmulator;

public class TestPubSubMessage {
  private static final Logger log = LoggerFactory.getLogger(TestPubSubMessage.class);
  private final PubSubEmulator pubsubEmulator = new PubSubEmulator();
  private String receivedMessage = null;

  public TestPubSubMessage() throws IOException {}

  public CaseActionEvent getPubSubCaseActionEventStatus() throws IOException {

    MessageReceiver receiver =
        (PubsubMessage message, AckReplyConsumer consumer) -> {
          // Handle incoming message, then ack the received message.
          log.info("Id: " + message.getMessageId());
          log.info("Data: " + message.getData().toStringUtf8());
          receivedMessage = message.getData().toStringUtf8();
          log.info("Received : " + receivedMessage);
          consumer.ack();
        };
    Subscriber subscriber = pubsubEmulator.getEmulatorSubscriberForCaseActionEventStatus(receiver);
    Thread t = new Thread(() -> subscriber.startAsync().awaitRunning());
    ExecutorService service = Executors.newSingleThreadExecutor();
    service.execute(t);
    while (receivedMessage == null) {
      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    }
    subscriber.stopAsync().awaitTerminated();
    service.shutdown();
    System.out.println(receivedMessage);
    ObjectMapper objectMapper = new ObjectMapper();
    return objectMapper.readValue(receivedMessage, CaseActionEvent.class);
  }
}
