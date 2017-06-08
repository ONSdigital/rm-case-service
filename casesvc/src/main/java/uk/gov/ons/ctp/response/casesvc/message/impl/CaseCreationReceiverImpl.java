package uk.gov.ons.ctp.response.casesvc.message.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.CaseCreationReceiver;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * Receive a new case from the Collection Exercise service.
 *
 */
@MessageEndpoint
@Slf4j
public class CaseCreationReceiverImpl implements CaseCreationReceiver {
  @Autowired
  private CaseService caseService;

  @Override
  @ServiceActivator(inputChannel = "caseTransformed", adviceChain = "caseRetryAdvice")
  public void acceptSampleUnit(SampleUnitParent caseCreation) {
    log.debug("received CaseCreation Message from queue");
    caseService.createInitialCase(caseCreation);
  }
}
