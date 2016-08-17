package uk.gov.ons.ctp.response.print.service;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * simple interface for the service that deals with received feedback from handlers
 *
 */
public interface PrintService {
  /**
   * Accept an instruction to print from actionsvc
   * @param instruction the ActionInstruction containing...
   */
 void acceptInstruction(ActionInstruction instruction);
}
