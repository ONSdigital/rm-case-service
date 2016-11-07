package uk.gov.ons.ctp.response.action.export.message;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;

/**
 * Service responsible for publishing an action feedback message to the action
 * service.
 *
 */
public interface ActionFeedbackPublisher {
  /**
   * To place an ActionFeedback message on the outbound channel
   * actionFeedbackOutbound.
   *
   * @param actionFeedback the ActionFeedback to put on the outbound channel.
   * @return ActionFeedback sent.
   */
  ActionFeedback sendActionFeedback(ActionFeedback actionFeedback);
}
