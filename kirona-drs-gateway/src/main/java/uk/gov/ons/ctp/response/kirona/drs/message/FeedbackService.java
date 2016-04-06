package uk.gov.ons.ctp.response.kirona.drs.message;

import org.springframework.integration.annotation.Publisher;
import org.springframework.messaging.handler.annotation.Header;

import javax.inject.Named;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

@Named
public class FeedbackService {
  @Publisher(channel="actionFeedbackOutbound")
  public ActionFeedback sendRequest() {
    ActionFeedback actionFeedback = new ActionFeedback();
    // TODO Define attributes
    return actionFeedback;
  }
}
