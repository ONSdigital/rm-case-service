package uk.gov.ons.ctp.response.casesvc.service;

/**
 * A Service which utilises the ActionSvc via RESTful client calls
 *
 * @author Chris Parker
 */
public interface ActionSvcClientService {

  /**
   * Make use of the ActionService to create and post a new Action for a given
   * caseId according to Category actionType and CaseEvent createdBy values
   *
   * @param actionType action type
   * @param caseId Integer caseId
   * @param createdBy who did this
   */
  void createAndPostAction(String actionType, int caseId, String createdBy);

}
