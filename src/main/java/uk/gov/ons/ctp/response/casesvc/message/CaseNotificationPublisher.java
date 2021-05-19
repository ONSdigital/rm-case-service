package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutureCallback;
import com.google.api.core.ApiFutures;
import com.google.api.gax.rpc.ApiException;
import com.google.cloud.pubsub.v1.Publisher;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import com.google.pubsub.v1.PubsubMessage;
import java.io.IOException;
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
        ApiFutures.addCallback(
            messageIdFuture,
            new ApiFutureCallback<>() {
              @Override
              public void onFailure(Throwable throwable) {
                if (throwable instanceof ApiException) {
                  ApiException apiException = ((ApiException) throwable);
                  log.with("error", apiException.getStatusCode().getCode())
                      .error("Case Notification sent failure");
                }
                log.with("message", message).error("Error Publishing pubsub message");
              }

              @Override
              public void onSuccess(String messageId) {
                // Once published, returns server-assigned message ids (unique within the topic)
                log.with("messageId", messageId).info("Case Notification sent successfully");
              }
            },
            MoreExecutors.directExecutor());
      } finally {
        publisher.shutdown();
        pubSub.shutdown();
      }
    } catch (JsonProcessingException e) {
      log.with("case_notification", caseNotificationDTO)
          .error("Error while case_notification can not be parsed.");
      throw new RuntimeException(e);
    } catch (IOException e) {
      log.error("PubSub Error while processing Case Notification", e);
      throw new RuntimeException(e);
    }
  }
}
