package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.representation.ActionPlanMappingDTO;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

/**
 * The REST endpoint controller for ActionPlanMapping
 */
@RestController
@RequestMapping(value = "/actionplanmappings", produces = "application/json")
@Slf4j
public final class ActionPlanMappingEndpoint implements CTPEndpoint {

  @Autowired
  private ActionPlanMappingService actionPlanMappingService;

  @Autowired
  private CaseTypeService caseTypeService;

  @Autowired
  private MapperFacade mapperFacade;


  /**
   * the GET endpoint to find a actionplanmapping by id
   * @param actionPlanMappingId to find by
   * @return the actionplanmapping or null if not found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{mappingId}", method = RequestMethod.GET)
  public ActionPlanMappingDTO findActionPlanMappingByActionPlanMappingId(@PathVariable("mappingId") final Integer actionPlanMappingId) throws CTPException {
    log.info("Entering findActionPlanMappingByActionPlanMappingId with {}", actionPlanMappingId);
    ActionPlanMapping actionPlanMapping = actionPlanMappingService.findActionPlanMapping(actionPlanMappingId);
    if (actionPlanMapping == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "ActionPlanMapping not found for id %s", actionPlanMappingId);
    }
    return mapperFacade.map(actionPlanMapping, ActionPlanMappingDTO.class);
  }
  
  /**
   * the GET endpoint to find actionplanmappings for a given case type instance
   * @param caseTypeId to find by
   * @return the actionplanmappings
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/casetype/{caseTypeId}", method = RequestMethod.GET)
  public List<ActionPlanMappingDTO> findActionPlanMappingByCaseTypeId(@PathVariable("caseTypeId") final Integer caseTypeId) throws CTPException {
    log.info("Entering findActionPlanMappingByCaseTypeId with {}", caseTypeId);
    CaseType caseType = caseTypeService.findCaseTypeByCaseTypeId(caseTypeId);
    if (caseType == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "CaseType not found for id %s", caseTypeId);
    }
    List<ActionPlanMapping> actionPlanMappings = actionPlanMappingService.findActionPlanMappingsForCaseType(caseTypeId);
    List<ActionPlanMappingDTO> actionPlanMappingDTOs = mapperFacade.mapAsList(actionPlanMappings, ActionPlanMappingDTO.class);
    return actionPlanMappingDTOs;
  }

}
