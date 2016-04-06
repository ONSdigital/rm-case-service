package uk.gov.ons.ctp.response.kirona.drs.message;

import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

@Slf4j
@MessageEndpoint
public class InstructionService {
  @ServiceActivator(inputChannel="instructionTransformed")
  public void acceptInstruction(ActionInstruction instruction) {
    log.debug("Entering acceptInstruction with instruction {}", instruction);
    // TODO From instruction, get the info required to call the Kirona WS
  }
}
