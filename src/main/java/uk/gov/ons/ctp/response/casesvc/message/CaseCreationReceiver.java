package uk.gov.ons.ctp.response.casesvc.message;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/** Receive a new case from the Collection Exercise service. */
@MessageEndpoint
public class CaseCreationReceiver {
  private static final Logger log = LoggerFactory.getLogger(CaseCreationReceiver.class);

  @Autowired private CaseService caseService;

  /**
   * To process SampleUnitParents read from queue
   *
   * @param caseCreation the java representation of the message body
   */
  @Transactional
  @ServiceActivator(inputChannel = "caseTransformed", adviceChain = "caseRetryAdvice")
  public void acceptSampleUnit(SampleUnitParent caseCreation) {
    log.debug("received CaseCreation Message from queue");
    caseService.createInitialCase(caseCreation);
  }
}
