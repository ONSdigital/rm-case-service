package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;

/**
 * The CaseGroup Service interface defines all business behaviours for operations
 * on the CaseGroup entity model.
 */
public interface CaseGroupService extends CTPService {

  /**
   * Find CaseGroup by unique Id.
   *
   * @param caseGroupId id of the case group to find
   * @return CaseGroup entity or null
   */
  CaseGroup findCaseGroupByCaseGroupId(Integer caseGroupId);

  /**
   * Find CaseGroup by uprn
   *
   * @param uprn of the case groups to find
   * @return List of CaseGroup entities or empty list if none
   */
   List<CaseGroup> findCaseGroupsByUprn(Long uprn);
}
