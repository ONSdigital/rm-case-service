package uk.gov.ons.ctp.response.print.service.impl;

import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.print.service.PrintService;

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
