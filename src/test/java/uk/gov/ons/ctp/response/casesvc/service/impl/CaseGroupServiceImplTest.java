package uk.gov.ons.ctp.response.casesvc.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupAuditService;

import java.util.UUID;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;


@RunWith(MockitoJUnitRunner.class)
public class CaseGroupServiceImplTest {

  @InjectMocks
  private CaseGroupServiceImpl caseGroupService;

  @Mock
  private CaseGroupRepository caseGroupRepo;

  @Mock
  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStatusTransitionManager;

  @Mock
  private CaseGroupAuditService caseGroupAuditService;

  @Test
  public void givenCaseGroupStatusWhenCaseGroupStatusTransitionedThenTransitionIsSaved() throws Exception {
    // Given
    CaseGroup caseGroup = CaseGroup.builder()
        .id(UUID.randomUUID())
        .collectionExerciseId(UUID.randomUUID())
        .partyId(UUID.randomUUID())
        .sampleUnitRef("12345")
        .sampleUnitType("B")
        .status(CaseGroupStatus.NOTSTARTED).build();

    CategoryDTO.CategoryName categoryName = CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED;
    given(caseGroupStatusTransitionManager.transition(caseGroup.getStatus(), categoryName)).willReturn(CaseGroupStatus.COMPLETE);

    // When
    caseGroupService.transitionCaseGroupStatus(caseGroup, categoryName, caseGroup.getPartyId());

    // Then
    verify(caseGroupRepo).saveAndFlush(caseGroup);
  }

  @Test
  public void givenCaseGroupStatusWhenCaseGroupStatusTransitionedThenTransitionIsAudited() throws Exception {
    // Given
    CaseGroup caseGroup = CaseGroup.builder()
        .id(UUID.randomUUID())
        .collectionExerciseId(UUID.randomUUID())
        .partyId(UUID.randomUUID())
        .sampleUnitRef("12345")
        .sampleUnitType("B")
        .status(CaseGroupStatus.NOTSTARTED).build();

    CategoryDTO.CategoryName categoryName = CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED;
    given(caseGroupStatusTransitionManager.transition(caseGroup.getStatus(), categoryName)).willReturn(CaseGroupStatus.COMPLETE);

    // When
    caseGroupService.transitionCaseGroupStatus(caseGroup, categoryName, caseGroup.getPartyId());

    // Then
    verify(caseGroupAuditService).updateAuditTable(caseGroup, caseGroup.getPartyId());
  }

}