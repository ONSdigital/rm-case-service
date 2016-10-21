package uk.gov.ons.ctp.response.action.export.message.impl;

import java.util.stream.Collectors;

import javax.inject.Inject;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.message.ActionExportReceiver;
import uk.gov.ons.ctp.response.action.export.service.ActionExportService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Service implementation responsible for receipt of action export instructions.
 * See Spring Integration flow for details of InstructionTransformed inbound
 * queue.
 *
 */
@MessageEndpoint
@Slf4j
public class ActionExportReceiverImpl implements ActionExportReceiver {

  @Inject
  private ActionExportService actionExportService;

  @Override
  @ServiceActivator(inputChannel = "instructionTransformed")
  public void acceptInstruction(ActionInstruction instruction) {
    log.debug("Instructed with action requests for action ids : [{}]",
        instruction.getActionRequests().getActionRequests().stream()
            .map(a -> a.getActionId().toString())
            .collect(Collectors.joining(",")));
    actionExportService.acceptInstruction(instruction);
  }
}
