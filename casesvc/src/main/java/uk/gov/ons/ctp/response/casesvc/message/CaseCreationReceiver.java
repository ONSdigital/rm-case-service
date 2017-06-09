package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;


public interface CaseCreationReceiver {

  /**
   * Method called with the deserialised message
   *
   * @param sampleunit The java representation of the message body
   */
  void acceptSampleUnit(SampleUnitParent caseCreation);

}

