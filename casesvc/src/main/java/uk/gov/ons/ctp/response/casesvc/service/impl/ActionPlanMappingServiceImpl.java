package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionPlanMappingRepository;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;

/**
 * An ActionPlanMappingService implementation which encapsulates all business logic
 * operating on the ActionPlanMapping entity model.
 */
@Named
@Slf4j
public class ActionPlanMappingServiceImpl implements ActionPlanMappingService {

  /**
   * Spring Data Repository for ActionPlanMapping entities.
   */
  @Inject
  private ActionPlanMappingRepository actionPlanMappingRepo;

  /**
   * Return all ActionPlanMappings.
   *
   * @return List of ActionPlanMapping entities or empty List
   */
  @Override
  public List<ActionPlanMapping> findActionPlanMappingsForCaseType(Integer caseTypeId) {
    log.debug("Entering findActionPlanMappingsForCaseType");
    return actionPlanMappingRepo.findByCaseTypeId(caseTypeId);
  }

  /**
   * Find ActionPlanMapping by unique Id.
   *
   * @param actionPlanMappingId ActionPlanMapping Id Integer
   * @return ActionPlanMapping entity or null
   */
  @Override
  public ActionPlanMapping findActionPlanMapping(Integer actionPlanMappingId) {
    log.debug("Entering findActionPlanMapping with {}", actionPlanMappingId);
    return actionPlanMappingRepo.findOne(actionPlanMappingId);
  }

}
