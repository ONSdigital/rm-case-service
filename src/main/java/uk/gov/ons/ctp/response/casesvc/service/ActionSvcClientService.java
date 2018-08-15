package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;
import java.util.UUID;
import uk.gov.ons.ctp.response.action.representation.ActionPlanDTO;

/** A Service which utilises the ActionSvc via RESTful client calls */
public interface ActionSvcClientService {

  /**
   * Make use of the ActionService to create and post a new Action for a given caseId according to
   * Category actionType and CaseEvent createdBy values
   *
   * @param actionType action type
   * @param caseId the UUID caseId
   * @param createdBy who did this
   */
  void createAndPostAction(String actionType, UUID caseId, String createdBy);

  /** */
  List<ActionPlanDTO> getActionPlans(UUID collectionExerciseId, boolean activeEnrolments);
}
