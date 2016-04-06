package uk.gov.ons.ctp.response.kirona.drs.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.kirona.drs.service.KironaDrsService;

import javax.inject.Named;

/**
 * Created by philippe.brossier on 4/6/16.
 */
@Named
@Slf4j
public class KironaDrsServiceImpl implements KironaDrsService {

  @Override public ActionFeedback createVisitJob(ActionRequest anActionRequest) {
    return null;
  }

  @Override public ActionFeedback updateVisitJob(ActionRequest anActionRequest) {
    return null;
  }

  @Override public ActionFeedback cancelVisitJob(ActionRequest anActionRequest) {
    return null;
  }
}
