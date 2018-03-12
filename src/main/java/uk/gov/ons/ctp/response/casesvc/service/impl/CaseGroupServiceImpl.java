package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupAuditService;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

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

}
