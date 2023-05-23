package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.ObjectConverter;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;

/** The REST endpoint controller for CaseSvc CaseGroups */
@RestController
@RequestMapping(value = "/casegroups", produces = "application/json")
public final class CaseGroupEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseGroupEndpoint.class);

  private CaseGroupService caseGroupService;
  private CaseService caseService;
  private CategoryService categoryService;
  private StateTransitionManager<CaseGroupStatus, CategoryName> caseGroupStatusTransitionManager;

  /** Constructor for CaseGroupEndpoint */
  @Autowired
  public CaseGroupEndpoint(
      final CaseService caseService,
      final CaseGroupService caseGroupService,
      final CategoryService categoryService,
      final StateTransitionManager<CaseGroupStatus, CategoryName>
          caseGroupStatusTransitionManager) {
    this.caseService = caseService;
    this.caseGroupService = caseGroupService;
    this.categoryService = categoryService;
    this.caseGroupStatusTransitionManager = caseGroupStatusTransitionManager;
  }

  /**
   * the GET endpoint to find CaseGroup by caseGroupId
   *
   * @param caseGroupId UUID to find by
   * @return the casegroup found
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/{caseGroupId}", method = RequestMethod.GET)
  public CaseGroupDTO findCaseGroupById(@PathVariable("caseGroupId") final UUID caseGroupId)
      throws CTPException {
    log.with("case_group_id").debug("Entering findCaseGroupById");
    CaseGroup caseGroupObj = caseGroupService.findCaseGroupById(caseGroupId);
    if (caseGroupObj == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("CaseGroup not found for casegroup id %s", caseGroupId));
    }

    return ObjectConverter.caseGroupDTO(caseGroupObj);
  }

  @RequestMapping(value = "/cases/{collectionExerciseId}", method = RequestMethod.GET)
  public ResponseEntity findNumberOfCases(
      @PathVariable("collectionExerciseId") final UUID collectionExerciseId) throws CTPException {
    log.with("collectionExerciseId", collectionExerciseId)
        .debug("Finding number of cases against collectionExercise");
    Long numberOfCases =
        caseGroupService.getNumberOfCasesAgainstCollectionExerciseId(collectionExerciseId);
    return ResponseEntity.ok(numberOfCases);
  }

  @RequestMapping(value = "/cases/{collectionExerciseId}/all", method = RequestMethod.GET)
  public ResponseEntity findAllCasesForCollectionExercise(
      @PathVariable("collectionExerciseId") final UUID collectionExerciseId) throws CTPException {
    // TODO: This endpoint needs to be combined with findNumberOfCases and given a sensible
    // implementation.
    // This is being added now to fix a production issue quickly.
    log.with("collectionExerciseId", collectionExerciseId)
        .debug("Finding all cases against collectionExercise");
    Long numberOfCases =
        caseGroupService.getAllCasesAgainstCollectionExerciseId(collectionExerciseId);
    return ResponseEntity.ok(numberOfCases);
  }

  /**
   * the GET endpoint to find CaseGroups by partyid UUID
   *
   * @param partyId to find by
   * @return the casegroups found
   */
  @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
  public ResponseEntity<List<CaseGroupDTO>> findCaseGroupsByPartyId(
      @PathVariable("partyId") final UUID partyId) {
    log.with("party_id", partyId).debug("Retrieving case groups by party id");

    List<CaseGroup> caseGroupList = caseGroupService.findCaseGroupByPartyId(partyId);

    if (CollectionUtils.isEmpty(caseGroupList)) {
      return ResponseEntity.noContent().build();
    }

    List<CaseGroupDTO> resultList =
        caseGroupList.stream().map(ObjectConverter::caseGroupDTO).collect(Collectors.toList());

    return ResponseEntity.ok(resultList);
  }

  /**
   * the GET endpoint to find Case Group transitions by collectionExerciseId and ruRef
   *
   * @param collectionExerciseId UUID to find by
   * @param ruRef String to find by
   * @return the transitions available and the events needed to put it in this state for the
   *     caseGroupId
   * @throws CTPException something went wrong
   */
  @RequestMapping(value = "/transitions/{collectionExerciseId}/{ruRef}", method = RequestMethod.GET)
  public Map<CategoryName, CaseGroupStatus> findTransitionsByCollectionExerciseIdAndRuRef(
      @PathVariable("collectionExerciseId") final UUID collectionExerciseId,
      @PathVariable("ruRef") final String ruRef)
      throws CTPException {
    log.with("collection_exercise_id", collectionExerciseId)
        .with("ru_ref", ruRef)
        .debug("Retrieving casegroup transistions");
    CaseGroup caseGroupObj =
        caseGroupService.findCaseGroupByCollectionExerciseIdAndRuRef(collectionExerciseId, ruRef);
    if (caseGroupObj == null) {
      throw new CTPException(
          CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format(
              "CaseGroup not found for collectionExerciseId %s, ruRef %s",
              collectionExerciseId, ruRef));
    }

    CaseGroupStatus caseGroupStatus = caseGroupObj.getStatus();

    return caseGroupStatusTransitionManager.getAvailableTransitions(caseGroupStatus);
  }

  private boolean isValidEvent(String event) {
    return Arrays.stream(CategoryName.values()).map(Enum::name).filter(e -> e.equals(event)).count()
        == 1;
  }

  /**
   * Deletes all case, caseevent and casegroup data for a particular collection exercise
   *
   * @param collectionExerciseId The Collection Exercise UUID to delete for
   * @return An appropriate HTTP repsonse code
   */
  @DeleteMapping("collectionExercises/{collectionExerciseId}")
  public ResponseEntity<String> deleteCaseDataByCollectionExercise(
      @PathVariable UUID collectionExerciseId) throws CTPException {

    log.with("collection_exercise_id", collectionExerciseId).info("Deleting cases");

    Long caseGroupCount =
        caseGroupService.getAllCasesAgainstCollectionExerciseId(collectionExerciseId);
    if (caseGroupCount == 0) {
      log.with("collection_exercise_id", collectionExerciseId)
          .info("No content found for Collection Exercise ID");
      return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    caseGroupService.deleteCaseGroupByCollectionExerciceId(collectionExerciseId);

    log.with("collection_exercise_id", collectionExerciseId).info("Delete successful");

    return ResponseEntity.ok("Deleted Successfully");
  }
}
