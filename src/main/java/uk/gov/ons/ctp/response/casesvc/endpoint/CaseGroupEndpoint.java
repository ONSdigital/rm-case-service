package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/** The REST endpoint controller for CaseSvc CaseGroups */
@RestController
@RequestMapping(value = "/casegroups", produces = "application/json")
public final class CaseGroupEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseGroupEndpoint.class);

  private CaseGroupService caseGroupService;
  private CaseService caseService;
  private CategoryService categoryService;
  private StateTransitionManager<CaseGroupStatus, CategoryName> caseGroupStatusTransitionManager;
  private MapperFacade mapperFacade;

  /** Constructor for CaseGroupEndpoint */
  @Autowired
  public CaseGroupEndpoint(
      final CaseService caseService,
      final CaseGroupService caseGroupService,
      final CategoryService categoryService,
      final StateTransitionManager<CaseGroupStatus, CategoryName> caseGroupStatusTransitionManager,
      final @Qualifier("caseSvcBeanMapper") MapperFacade mapperFacade) {
    this.caseService = caseService;
    this.caseGroupService = caseGroupService;
    this.categoryService = categoryService;
    this.caseGroupStatusTransitionManager = caseGroupStatusTransitionManager;
    this.mapperFacade = mapperFacade;
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

    return mapperFacade.map(caseGroupObj, CaseGroupDTO.class);
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
        caseGroupList
            .stream()
            .map(cg -> mapperFacade.map(cg, CaseGroupDTO.class))
            .collect(Collectors.toList());

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
}
