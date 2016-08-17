package uk.gov.ons.ctp.response.print.service.impl;


import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.print.service.PrintService;

/**
 * Accept feedback from handlers
 */
@Slf4j
@Named
public class PrintServiceImpl implements PrintService {



  @Override
  public void acceptInstruction(ActionInstruction instruction) {
    log.debug("accepted instruction in service");

  }

}
