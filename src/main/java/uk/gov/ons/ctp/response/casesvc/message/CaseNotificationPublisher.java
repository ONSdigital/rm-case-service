package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import com.google.protobuf.ByteString;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication;
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

  @Autowired private CaseSvcApplication.PubsubOutboundGateway messagingGateway;

  /**
   * sends case notification to action service via pubsub
   *
   * @param caseNotificationDTO the CaseNotification to put on the pubsub
   */
  public void sendNotification(CaseNotificationDTO caseNotificationDTO) {
    log.with("case_notification", caseNotificationDTO).info("sending CaseNotification to pubsub");
    try {
      String message = objectMapper.writeValueAsString(caseNotificationDTO);
      log.info("Publishing message to pubsub");
      messagingGateway.sendToPubsub(message);
    } catch (JsonProcessingException e) {
      log.with("case_notification", caseNotificationDTO)
          .error("Error while case_notification can not be parsed.");
      throw new RuntimeException(e);
    }
  }
}
