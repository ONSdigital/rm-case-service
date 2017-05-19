package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.definition.CaseCreation;

/**
 * Interface for the receipt of case messages from the Spring Integration
 * inbound message queue
 */
public interface CaseCreationReceiver {

  /**
   * Method called with the deserialised message
   *
   * @param sampleunit The java representation of the message body
   */
  void process(CaseCreation caseCreation);

}

