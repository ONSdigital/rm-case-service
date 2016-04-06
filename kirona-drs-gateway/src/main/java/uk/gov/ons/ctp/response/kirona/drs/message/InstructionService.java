package uk.gov.ons.ctp.response.kirona.drs.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequests;

import java.util.List;

@Slf4j
@MessageEndpoint
public class InstructionService {
  @ServiceActivator(inputChannel="actionInstructionTransformed")
  public void acceptInstruction(ActionInstruction instruction) {
    log.debug("Entering acceptInstruction with instruction {}", instruction);
    ActionRequests actionRequests = instruction.getActionRequests();
    if (actionRequests != null) {
      List<ActionRequest> actionRequestList = actionRequests.getActionRequests();
      if (actionRequestList != null && !actionRequestList.isEmpty()) {
        for (ActionRequest anActionRequest: actionRequestList) {
          log.debug("dealing with actionRequest {}", anActionRequest);
          //TODO
        }
      }
    }
  }
}
