package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 * The REST endpoint controller for CaseSvc CaseGroups
 */
@RestController
@RequestMapping(value = "/casegroups", produces = "application/json")
@Slf4j
public final class CaseGroupEndpoint implements CTPEndpoint {

  public static final String ERRORMSG_CASEGROUPNOTFOUND = "CaseGroup not found for";

  @Autowired
  private CaseGroupService caseGroupService;

  @Autowired
  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStatusTransitionManager;

  @Qualifier("caseSvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

 /**
   * the GET endpoint to find CaseGroups by caseGroupId
   *
   * @param caseGroupId UUID to find by
   * @return the casegroups found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseGroupId}", method = RequestMethod.GET)
  public CaseGroupDTO findCaseGroupById(@PathVariable("caseGroupId") final UUID caseGroupId)  throws CTPException {
    log.info("Entering findCaseGroupById with {}", caseGroupId);

    CaseGroup caseGroupObj = caseGroupService.findCaseGroupById(caseGroupId);
    if (caseGroupObj == null) {
        throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, String.format("%s casegroup id %s",
                ERRORMSG_CASEGROUPNOTFOUND, caseGroupId));
    }

    return mapperFacade.map(caseGroupObj, CaseGroupDTO.class);
  }

    /**
     * the GET endpoint to find Case Group transitions by collectionExerciseId and ruRef
     *
     * @param collectionExerciseId UUID to find by
     * @param ruRef String to find by
     * @return the transitions available and the events needed to put it in this state for the caseGroupId
     * @throws CTPException something went wrong
     */
    @RequestMapping(value = "/transitions/{collectionExerciseId}/{ruRef}", method = RequestMethod.GET)
    public Map<CategoryDTO.CategoryName, CaseGroupStatus> findTransitionsByCollectionExerciseIdAndRuRef(
        @PathVariable("collectionExerciseId") final UUID collectionExerciseId,
        @PathVariable("ruRef") final String ruRef)
        throws CTPException {
          log.info("Entering findTransitionsByCollexIdAndRuRef with collectionExerciseId {}, ruRef {}",
              collectionExerciseId, ruRef);

          CaseGroup caseGroupObj = caseGroupService.findCaseGroupByCollectionExerciseIdAndRuRef(collectionExerciseId, ruRef);
          if (caseGroupObj == null) {
              throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, String.format("%s collectionExerciseId %s, ruRef %s",
                      ERRORMSG_CASEGROUPNOTFOUND, collectionExerciseId, ruRef));
          }
          CaseGroupStatus caseGroupStatus = caseGroupObj.getStatus();

          return caseGroupStatusTransitionManager.getAvailableTransitions(caseGroupStatus);
    }

  /**
   * the PUT endpoint to change Case Group status from a given Case Group event
   *
   * @param collectionExerciseId UUID to find by
   * @param ruRef String to find by
   * @param caseGroupEvent Name of event
   * @return 200 if all is ok, 400 for bad request
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/transitions/{collectionExerciseId}/{ruRef}", method = RequestMethod.PUT)
  public ResponseEntity<?> changeCaseGroupStatus(
      @PathVariable("collectionExerciseId") final UUID collectionExerciseId,
      @PathVariable("ruRef") final String ruRef,
      @RequestBody CaseGroupEvent caseGroupEvent)
      throws CTPException {
        log.info("Entering changeCaseGroupStatus with collectionExerciseId {}, ruRef {}, caseGroupEvent {}",
            collectionExerciseId, ruRef, caseGroupEvent.getEvent());

        CaseGroup caseGroupObj = caseGroupService.findCaseGroupByCollectionExerciseIdAndRuRef(collectionExerciseId, ruRef);
        if (caseGroupObj == null) {
          throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, String.format("%s collectionExerciseId %s, ruRef %s",
              ERRORMSG_CASEGROUPNOTFOUND, collectionExerciseId, ruRef));
        }

        caseGroupService.transitionCaseGroupStatus(caseGroupObj, CategoryDTO.CategoryName.valueOf(caseGroupEvent.getEvent()),
            caseGroupObj.getPartyId());

        return ResponseEntity.ok().build();
  }
}
