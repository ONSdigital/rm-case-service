package uk.gov.ons.ctp.response.action.export.message.impl;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.message.InstructionReceiver;
import uk.gov.ons.ctp.response.action.export.service.PrintService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * The entry point for inbound feedback messages from SpringIntegration. See the
 * integration-context.xml
 *
 * This is just an annotated class that acts as the initial receiver - the work
 * is done in the feedbackservice, but having this class in this package keeps
 * spring integration related entry/exit points in one logical location
 */
@MessageEndpoint
@Slf4j
public class InstructionReceiverImpl implements InstructionReceiver {
  @Inject
  private PrintService printService;

  @Override
  @ServiceActivator(inputChannel = "printInstructionTransformed")
  public void acceptInstruction(ActionInstruction instruction) {
    log.debug("Instructed with action requests for action ids : [{}]", 
        instruction.getActionRequests().getActionRequests().stream()
        .map(a -> a.getActionId().toString())
        .collect(Collectors.joining(",")));
    printService.acceptInstruction(instruction);
  }
}
