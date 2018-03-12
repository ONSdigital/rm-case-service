package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;
import java.util.UUID;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
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
  void transitionCaseGroupStatus(final CaseGroup caseGroup, final CategoryDTO.CategoryName categoryName, final UUID partyId) throws CTPException;
}
