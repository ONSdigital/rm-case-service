package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionPlanMappingRepository;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;

/**
 * An ActionPlanMappingService implementation which encapsulates all business logic
 * operating on the ActionPlanMapping entity model.
 */
@Service
@Slf4j
public class ActionPlanMappingServiceImpl implements ActionPlanMappingService {

  /**
   * Spring Data Repository for ActionPlanMapping entities.
   */
  @Autowired
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
