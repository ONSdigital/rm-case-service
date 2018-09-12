package uk.gov.ons.ctp.response.casesvc.message.impl;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.message.CaseCreationReceiver;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/** Receive a new case from the Collection Exercise service. */
@MessageEndpoint
public class CaseCreationReceiverImpl implements CaseCreationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseCreationReceiverImpl.class);

  @Autowired private CaseService caseService;

  @Transactional(propagation = Propagation.REQUIRED, readOnly = false, value = "transactionManager")
  @Override
  @ServiceActivator(inputChannel = "caseTransformed", adviceChain = "caseRetryAdvice")
  public void acceptSampleUnit(SampleUnitParent caseCreation) {
    log.debug("received CaseCreation Message from queue");
    caseService.createInitialCase(caseCreation);
  }
}
