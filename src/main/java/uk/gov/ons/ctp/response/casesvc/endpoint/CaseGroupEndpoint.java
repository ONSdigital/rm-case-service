package uk.gov.ons.ctp.response.casesvc.endpoint;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDetailsDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The REST endpoint controller for CaseSvc CaseGroups
 */
@RestController
@RequestMapping(value = "/casegroups", produces = "application/json")
@Slf4j
public final class CaseGroupEndpoint implements CTPEndpoint {

    @Autowired
    private CaseGroupService caseGroupService;

    @Autowired
    private CaseService caseService;

    @Autowired
    private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStatusTransitionManager;

    @Autowired
    private CategoryService categoryService;

    @Qualifier("caseSvcBeanMapper")
    @Autowired
    private MapperFacade mapperFacade;

    /**
     * the GET endpoint to find CaseGroup by caseGroupId
     *
     * @param caseGroupId UUID to find by
     * @return the casegroup found
     * @throws CTPException something went wrong
     */
    @RequestMapping(value = "/{caseGroupId}", method = RequestMethod.GET)
    public CaseGroupDTO findCaseGroupById(@PathVariable("caseGroupId") final UUID caseGroupId) throws CTPException {
        log.info("Entering findCaseGroupById with {}", caseGroupId);

        CaseGroup caseGroupObj = caseGroupService.findCaseGroupById(caseGroupId);
        if (caseGroupObj == null) {
            throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
                    String.format("CaseGroup not found for casegroup id %s", caseGroupId));
        }

        return mapperFacade.map(caseGroupObj, CaseGroupDTO.class);
    }

    /**
     * the GET endpoint to find CaseGroups by partyid UUID
     *
     * @param partyId to find by
     * @return the casegroups found
     * @throws CTPException something went wrong
     */
    @RequestMapping(value = "/partyid/{partyId}", method = RequestMethod.GET)
    public ResponseEntity<List<CaseGroupDTO>> findCaseGroupsByPartyId(
            @PathVariable("partyId") final UUID partyId)
            throws CTPException {
        log.info("Entering findCaseGroupsByPartyId with {}", partyId);
        List<CaseGroup> caseGroupList = caseGroupService.findCaseGroupByPartyId(partyId);

        if (CollectionUtils.isEmpty(caseGroupList)) {
            return ResponseEntity.noContent().build();
        }

        List<CaseGroupDTO> resultList = caseGroupList.stream().map(cg -> mapperFacade.map(cg, CaseGroupDTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok(resultList);
    }

    /**
     * the GET endpoint to find Case Group transitions by collectionExerciseId and ruRef
     *
     * @param collectionExerciseId UUID to find by
     * @param ruRef                String to find by
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
            throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
                    String.format("CaseGroup not found for collectionExerciseId %s, ruRef %s", collectionExerciseId, ruRef));
        }
        CaseGroupStatus caseGroupStatus = caseGroupObj.getStatus();

        return caseGroupStatusTransitionManager.getAvailableTransitions(caseGroupStatus);
    }

    /**
     * the PUT endpoint to change Case Group status from a given Case Group event
     *
     * @param collectionExerciseId UUID to find by
     * @param ruRef                String to find by
     * @param caseGroupEvent       Name of event
     * @return 200 if all is ok, 400 for bad request
     * @throws CTPException something went wrong
     */
    @RequestMapping(value = "/transitions/{collectionExerciseId}/{ruRef}", method = RequestMethod.PUT)
    public ResponseEntity<?> changeCaseGroupStatus(
            @PathVariable("collectionExerciseId") final UUID collectionExerciseId,
            @PathVariable("ruRef") final String ruRef,
            @RequestBody CaseGroupEvent caseGroupEvent)
            throws CTPException {
        String event = caseGroupEvent.getEvent();
        log.info("Entering changeCaseGroupStatus with collectionExerciseId {}, ruRef {}, caseGroupEvent {}",
                collectionExerciseId, ruRef, event);

        if (!isValidEvent(event)) {
            throw new CTPException(CTPException.Fault.BAD_REQUEST, String.format("Invalid event %s", event));
        }
        CategoryDTO.CategoryName eventCategory = CategoryDTO.CategoryName.valueOf(event);

        CaseGroup caseGroupObj = caseGroupService.findCaseGroupByCollectionExerciseIdAndRuRef(collectionExerciseId, ruRef);
        if (caseGroupObj == null) {
            throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
                    String.format("CaseGroup not found for collectionExerciseId %s, ruRef %s", collectionExerciseId, ruRef));
        }
        List<Case> cases = caseService.findCasesByCaseGroupFK(caseGroupObj.getCaseGroupPK());
        Category category = categoryService.findCategory(eventCategory);
        for (Case c: cases) {
            CaseEvent caseEvent = CaseEvent.builder()
                    .caseFK(c.getCasePK())
                    .category(eventCategory)
                    .description(category.getShortDescription())
                    .createdBy(Constants.USER)
                    .build();
            caseService.createCaseEvent(caseEvent, c);
        }

        return ResponseEntity.ok().build();
    }

    private boolean isValidEvent(String event) {
        return Arrays.stream(CategoryDTO.CategoryName.values())
                .map(Enum::name)
                .filter(e -> e.equals(event))
                .count() == 1;
    }
}
