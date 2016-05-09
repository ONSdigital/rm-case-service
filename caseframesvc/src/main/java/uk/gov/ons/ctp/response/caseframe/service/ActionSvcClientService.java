package uk.gov.ons.ctp.response.caseframe.service;

import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;

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
   * @param category Category containing action type
   * @param caseId Integer caseId
   * @param caseEvent CaseEvent containing createdBy detail
   */
  void createAndPostAction(String actionType, int caseId, String createdBy);

  /**
   * Cancel any Actions existing for a caseId
   *
   * @param caseId Integer caseId
   */
  void cancelActions(int caseId);
}
