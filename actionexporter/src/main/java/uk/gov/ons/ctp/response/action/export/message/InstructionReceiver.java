package uk.gov.ons.ctp.response.action.export.message;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Interface for the receipt of feedback messages from the Spring Integration
 * inbound message queue
 */
public interface InstructionReceiver {

  /**
   * impl will be called with the deserialised AMQ message sent from downstream
   * handlers
   *
   * @param feedback the java representation of the AMQ message body
   */
  void acceptInstruction(ActionInstruction instruction);

}
