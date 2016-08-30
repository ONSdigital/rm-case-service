package uk.gov.ons.ctp.response.action.export.service.impl;

import uk.gov.ons.ctp.response.action.export.service.PrintService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * a dummy service impl
 *
 */
public class DummyPrintServiceImpl implements PrintService {

  @Override
  public void acceptInstruction(ActionInstruction instruction) {
    // Do nothing - here only as a concrete mock stand in
  }

}
