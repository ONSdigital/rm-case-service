package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;

/**
 * The CaseTypr Service interface defines all business behaviours for operations
 * on the CaseType entity model.
 */
public interface CaseTypeService extends CTPService {

  /**
   * Return all CaseTypes.
   *
   * @return List of CaseType entities or empty List
   */
  List<CaseType> findCaseTypes();

  /**
   * Find CaseType by unique Id.
   *
   * @param caseTypeId CaseType Id Integer
   * @return CaseType entity or null
   */
  CaseType findCaseTypeByCaseTypeId(Integer caseTypeId);

}
