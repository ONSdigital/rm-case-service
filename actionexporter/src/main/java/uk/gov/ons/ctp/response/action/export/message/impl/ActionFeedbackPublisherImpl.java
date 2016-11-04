package uk.gov.ons.ctp.response.action.export.message.impl;

import javax.inject.Named;

import org.springframework.integration.annotation.Publisher;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.message.ActionFeedbackPublisher;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

/**
 * Service implementation responsible for publishing an action feedback message
 * to the action service.
 *
 */
@Named
@Slf4j
public class ActionFeedbackPublisherImpl implements ActionFeedbackPublisher {

  @Override
  @Publisher(channel = "actionFeedbackOutbound")
  public ActionFeedback sendActionFeedback(ActionFeedback actionFeedback) {
    log.debug("Entering sendActionFeedback for actionId {} ", actionFeedback.getActionId());
    return actionFeedback;
  }
}
