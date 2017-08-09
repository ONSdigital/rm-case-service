package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;

/**
 * The reader of SampleUnitParents from queue
 */
public interface CaseCreationReceiver {
  /**
   * To process SampleUnitParents read from queue
   * @param caseCreation the java representation of the message body
   */
  void acceptSampleUnit(SampleUnitParent caseCreation);

}

