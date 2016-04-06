package uk.gov.ons.ctp.response.kirona.drs.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;
import uk.gov.ons.ctp.response.kirona.drs.service.KironaDrsService;

import javax.inject.Inject;
import java.util.List;

@Slf4j
@MessageEndpoint
public class InstructionService {

  @Inject
  private FeedbackService feedbackService;

  @Inject
  private KironaDrsService kironaDrsService;

  @ServiceActivator(inputChannel="actionInstructionTransformed")
  public void processInstruction(ActionInstruction instruction) {
    log.debug("Entering acceptInstruction with instruction {}", instruction);
    ActionRequests actionRequests = instruction.getActionRequests();
    if (actionRequests != null) {
      List<ActionRequest> actionRequestList = actionRequests.getActionRequests();
      if (actionRequestList != null && !actionRequestList.isEmpty()) {
        for (ActionRequest anActionRequest: actionRequestList) {
          log.debug("dealing with actionRequest {}", anActionRequest);
          String actionType = anActionRequest.getActionType();
          // TODO a switch on actionType to determine which method to call next
          ActionFeedback actionFeedback = kironaDrsService.createVisitJob(anActionRequest);
          feedbackService.sendFeedback(actionFeedback);
        }
      }
    }
  }
}
