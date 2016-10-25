package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Service responsible for dealing with action export requests
 *
 */
public interface ActionExportService {
  /**
   * Accept an instruction from actionsvc
   *
   * @param instruction the ActionInstruction containing.
   */
  void acceptInstruction(ActionInstruction instruction);
}
