package uk.gov.ons.ctp.response.kirona.drs.service;

import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

/**
 * Created by philippe.brossier on 4/6/16.
 */
public interface KironaDrsService {
  ActionFeedback createVisitJob(ActionRequest anActionRequest);
  ActionFeedback updateVisitJob(ActionRequest anActionRequest);
  ActionFeedback cancelVisitJob(ActionRequest anActionRequest);
}
