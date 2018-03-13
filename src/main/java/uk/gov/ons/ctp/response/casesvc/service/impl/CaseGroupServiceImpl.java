package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupAuditService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;
import uk.gov.ons.ctp.response.casesvc.service.CollectionExerciseSvcClientService;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

/**
 * A CaseGroupService implementation which encapsulates all business logic
 * operating on the CaseGroup entity model.
 */
@Service
@Slf4j
public class CaseGroupServiceImpl implements CaseGroupService {

  /**
   * Spring Data Repository for CaseGroup entities.
   */
  @Autowired
  private CaseGroupRepository caseGroupRepo;

  @Autowired
  private CollectionExerciseSvcClientService collectionExerciseSvcClientService;

  @Autowired
  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStatusTransitionManager;

  @Autowired
  private CaseGroupAuditService caseGroupAuditService;

  @Override
  public CaseGroup findCaseGroupByCaseGroupPK(final Integer caseGroupPK) {
    log.debug("Entering findCaseGroupByCaseGroupId with {}", caseGroupPK);
    return caseGroupRepo.findOne(caseGroupPK);
  }

  @Override
  public CaseGroup findCaseGroupById(final UUID id) {
    log.debug("Entering findCaseGroupById with {}", id);
    return caseGroupRepo.findById(id);
  }

  @Override
  public List<CaseGroup> findCaseGroupByPartyId(final UUID id) {
    log.debug("Entering findCaseGroupByPartyId with {}", id);
    return caseGroupRepo.findByPartyId(id);
  }

  @Override
  public CaseGroup findCaseGroupByCollectionExerciseIdAndRuRef(final UUID collectionExerciseId, final String ruRef) {
    log.debug("Entering findCaseGroupByCollectionExerciseIdAndRuRef for collectionExerciseId {}, ruRef {}");
    return caseGroupRepo.findCaseGroupByCollectionExerciseIdAndSampleUnitRef(collectionExerciseId, ruRef);
  }

  /**
   * Uses the state transition manager to transition the overarching casegroupstatus,
   * this is the status for the overall progress of the survey.
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public void transitionCaseGroupStatus(final CaseGroup caseGroup, final CategoryDTO.CategoryName categoryName, final UUID partyId) throws CTPException {
    CaseGroupStatus oldCaseGroupStatus = caseGroup.getStatus();

    CaseGroupStatus newCaseGroupStatus = caseGroupStatusTransitionManager.transition(oldCaseGroupStatus, categoryName);

    if (newCaseGroupStatus != null && !oldCaseGroupStatus.equals(newCaseGroupStatus)) {
      caseGroup.setStatus(newCaseGroupStatus);
      caseGroupRepo.saveAndFlush(caseGroup);
      caseGroupAuditService.updateAuditTable(caseGroup, partyId);
    }

  }

  /**
   * Use case ID to find case group
   * Use case group to find collectionEx
   * Then use CollEx service to find survey ID, then find all collexs for survey
   * Then get all case groups for the party ID where Collex ID is in list of collexs
   *
   * @param targetCase the case for which related case groups should be found
   * @return a list of other case groups related to the target by party and collection exercise
   * @throws CTPException thrown if database error etc
   */
  public List<CaseGroup> transitionOtherCaseGroups(final Case targetCase) throws CTPException {
    if (targetCase == null){
        throw new CTPException(CTPException.Fault.SYSTEM_ERROR, "Target case must be supplied");
    }
    CaseGroup caseGroup = caseGroupRepo.findOne(targetCase.getCaseGroupFK());
    if (caseGroup == null){
        throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, String.format("Cannot find case group %s", targetCase.getCaseGroupFK()));
    }
    CollectionExerciseDTO collectionExercise = collectionExerciseSvcClientService
            .getCollectionExercise(caseGroup.getCollectionExerciseId());
    if (collectionExercise == null){
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, String.format("Cannot find collection exercise %s", caseGroup.getCollectionExerciseId()));
    }
    // fetch all the collection exercises for a survey
    List<CollectionExerciseDTO> collectionExercises =
            collectionExerciseSvcClientService.getCollectionExercises(collectionExercise.getSurveyId());
    if (collectionExercises == null){
      // This will happen if say the collection exercise service is down
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, String.format("Cannot find collection exercises for survey %s", collectionExercise.getSurveyId()));
    }
    // get published collection exercise
    List<CollectionExerciseDTO> publishedCollexs =
            collectionExercises
                    .stream()
                    .filter(ce -> ce.getState().toString().equals("READY_FOR_LIVE")
                            || ce.getState().toString().equals("LIVE"))
                    .collect(Collectors.toList());
    // get list of collection exercise ids
    List<UUID> collExs = publishedCollexs.stream().map(CollectionExerciseDTO::getId).collect(Collectors.toList());
    // fetch party ID for the RU
    UUID partyId = targetCase.getPartyId();
    //select * from casesvc.casegroup where partyid = partyId and collectionexerciseid in collectionExercises
    return caseGroupRepo.retrieveByPartyIdInListOfCollEx(partyId, collExs);
  }

}
