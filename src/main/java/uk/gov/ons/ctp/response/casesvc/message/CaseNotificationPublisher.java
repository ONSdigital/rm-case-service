package uk.gov.ons.ctp.response.casesvc.message;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
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

  @Autowired private ObjectMapper objectMapper;

  @Autowired
  private CaseSvcApplication.PubSubOutboundActionCaseNotificationGateway messagingGateway;

  /**
   * sends case notification to action service via pubsub
   *
   * @param caseNotificationDTO the CaseNotification to put on the pubsub
   */
  public void sendNotification(CaseNotificationDTO caseNotificationDTO) {
    String caseId = caseNotificationDTO.getCaseId();
    String sampleUnitId = caseNotificationDTO.getSampleUnitId();
    log.with("case_notification", caseNotificationDTO).info("sending CaseNotification to PubSub");
    try {
      String message = objectMapper.writeValueAsString(caseNotificationDTO);
      log.with("case_id", caseId)
              .with("sampleUnitId", sampleUnitId)
              .info("Publishing message to PubSub for action case notification.");
      messagingGateway.sendToPubSub(message);
      log.with("case_id", caseId)
              .with("sampleUnitId", sampleUnitId)
              .info("Message published successfully for action case notification.");
    } catch (JsonProcessingException e) {
      log.with("case_notification", caseNotificationDTO)
          .error("Error while case_notification can not be parsed.");
      throw new RuntimeException(e);
    }
  }
}
