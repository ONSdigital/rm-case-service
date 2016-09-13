package uk.gov.ons.ctp.response.action.export.message;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Interface for the receipt of action export messages from the Spring Integration
 * inbound message queue
 */
public interface ActionExportReceiver {

  /**
   * Method called with the deserialised JMS message sent from downstream
   * handlers
   *
   * @param instruction The java representation of the JMS message body
   */
  void acceptInstruction(ActionInstruction instruction);

}
