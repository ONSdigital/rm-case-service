package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.representation.CaseNotificationDTO;

/**
 * Service implementation responsible for publishing case lifecycle events to notification channel
 */
@Service
public class CaseNotificationPublisher {
  private static final Logger log = LoggerFactory.getLogger(CaseNotificationPublisher.class);

  @Autowired AppConfig appConfig;

  @Autowired private PubSub pubSub;

  @Autowired private ObjectMapper objectMapper;
  /**
   * sends case notification to action service via pubsub
   *
   * @param caseNotificationDTO the CaseNotification to put on the pubsub
   */
  public void sendNotification(CaseNotificationDTO caseNotificationDTO) {
    log.with("case_notification", caseNotificationDTO).info("sending CaseNotification to pubsub");
    try {
      String message = objectMapper.writeValueAsString(caseNotificationDTO);
      ByteString data = ByteString.copyFromUtf8(message);
      PubsubMessage pubsubMessage = PubsubMessage.newBuilder().setData(data).build();
      Publisher publisher = pubSub.caseNotificationPublisher();
      try {
        log.with("publisher", publisher).info("Publishing message to pubsub");
        ApiFuture<String> messageIdFuture = publisher.publish(pubsubMessage);
        String messageId = messageIdFuture.get();
        log.with("messageId", messageId).info("Case Notification sent successfully");
      } finally {
        publisher.shutdown();
        pubSub.shutdown();
      }
    } catch (JsonProcessingException e) {
      log.with("case_notification", caseNotificationDTO)
          .error("Error while case_notification can not be parsed.");
      throw new RuntimeException(e);
    } catch (InterruptedException | ExecutionException | IOException e) {
      log.error("PubSub Error while processing Case Notification", e);
      throw new RuntimeException(e);
    }
  }
}
