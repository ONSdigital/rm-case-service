package uk.gov.ons.ctp.response.casesvc.service.action.email;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication.PubSubOutboundEmailGateway;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel;

@Service
public class NotifyEmailService {
  public static Logger log = LoggerFactory.getLogger(NotifyEmailService.class);

  @Autowired private ObjectMapper objectMapper;

  @Autowired private PubSubOutboundEmailGateway pubEmailPublisher;

  public void processEmail(NotifyModel notifyPayload) {

    log.info("Sending email notification to PubSub");

    try {
      String message = objectMapper.writeValueAsString(notifyPayload);
      pubEmailPublisher.sendToPubSub(message);
      log.info("Email notification sent to PubSub successfully. ");
    } catch (JsonProcessingException e) {
      log.error("Error converting an actionRequest to JSON", e);
      throw new RuntimeException(e);
    }
  }
}
