package uk.gov.ons.ctp.response.casesvc.service;

import java.util.UUID;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;

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
   * Find CaseGroup by uprn
   *
   * @param uprn of the case groups to find
   * @return List of CaseGroup entities or empty list if none
   */
   // TODO BRES - replace with?
   //List<CaseGroup> findCaseGroupsByUprn(Long uprn);
}
