package uk.gov.ons.ctp.response.action.export.service;

import java.math.BigInteger;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
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

  /**
   * Return a ActionRequestDocument by Id
   *
   * @param actionId of the required ActionRequestDocument.
   * @return the ActionRequestDocument found
   */
  ActionRequestDocument findActionRequestDocument(final BigInteger actionId);

  /**
   * Save a ActionRequestDocument
   *
   * @param actionRequest the ActionRequestDocument to save.
   * @return the ActionRequestDocument saved.
   */
  ActionRequestDocument save(final ActionRequestDocument actionRequest);

}
