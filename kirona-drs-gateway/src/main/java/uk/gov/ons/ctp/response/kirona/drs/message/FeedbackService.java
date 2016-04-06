package uk.gov.ons.ctp.response.kirona.drs.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.Publisher;
import org.springframework.messaging.handler.annotation.Header;

import javax.inject.Named;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

@Named
@Slf4j
public class FeedbackService {
  @Publisher(channel="actionFeedbackOutbound")
  public ActionFeedback sendFeedback(ActionFeedback actionFeedback) {
    log.debug("Entering sendFeedback...");
    return actionFeedback;
  }
}
