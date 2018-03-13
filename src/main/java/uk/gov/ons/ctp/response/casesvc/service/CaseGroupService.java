package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;
import java.util.UUID;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/**
 * The CaseGroup Service interface defines all business behaviours for operations
 * on the CaseGroup entity model.
 */
public interface CaseGroupService extends CTPService {

  /**
   * Find CaseGroup by caseGroupPK.
   *
   * @param caseGroupPK the CaseGroup Primary Key
   * @return CaseGroup entity or null
   */
  CaseGroup findCaseGroupByCaseGroupPK(Integer caseGroupPK);

  /**
   * Find CaseGroup by unique Id.
   *
   * @param id UUID of the case group to find
   * @return CaseGroup entity or null
   */
  CaseGroup findCaseGroupById(UUID id);

  /**
   * Find CaseGroups by party Id.
   *
   * @param id UUID of party
   * @return CaseGroup entity or null
   */
  List<CaseGroup> findCaseGroupByPartyId(UUID id);

  CaseGroup findCaseGroupByCollectionExerciseIdAndRuRef(final UUID collectionExerciseId, final String ruRef);

  /**
   * For a given event, this will update and audit a transition
   * @param caseGroup Case Group to transition
   * @param categoryName The name of the event for a transition
   * @param partyId party ID for auditing the transition
   * @throws CTPException if transition is not executed
   */
  void transitionCaseGroupStatus(CaseGroup caseGroup, CategoryDTO.CategoryName categoryName,
                                 UUID partyId) throws CTPException;

  /**
   *
   * @param targetCase the case for which related case groups should be found
   * @return a list of case groups related to the target by party and collection exercise
   * @throws CTPException thrown if an error occurs
   */
  List<CaseGroup> transitionOtherCaseGroups(Case targetCase) throws CTPException;

}
