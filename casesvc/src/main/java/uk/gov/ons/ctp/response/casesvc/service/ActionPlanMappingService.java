package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;

/**
 * The CaseTypr Service interface defines all business behaviours for operations
 * on the CaseType entity model.
 */
public interface ActionPlanMappingService extends CTPService {

  /**
   * Return all ActionPlanMapping for a given case type instance.
   *
   * @return List of ActionPlanMapping entities or empty List
   */
  List<ActionPlanMapping> findActionPlanMappingsForCaseType(Integer caseTypeId);

  /**
   * Find  a single ActionPlanMapping by unique mapping Id.
   *
   * @param mappingId the mappings id
   * @return ActionPlanMapping entity or null
   */
  ActionPlanMapping findActionPlanMapping(Integer mappingId);

}
