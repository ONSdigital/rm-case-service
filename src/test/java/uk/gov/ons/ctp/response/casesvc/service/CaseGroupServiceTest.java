package uk.gov.ons.ctp.response.casesvc.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyList;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;

import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.collection.exercise.representation.CollectionExerciseDTO;

@RunWith(MockitoJUnitRunner.class)
public class CaseGroupServiceTest {

  @InjectMocks private CaseGroupService caseGroupService;

  @Mock private CaseGroupRepository caseGroupRepo;

  @Mock private CollectionExerciseSvcClient collectionExerciseSvcClient;

  @Mock
  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>
      caseGroupStatusTransitionManager;

  @Mock private CaseGroupAuditService caseGroupAuditService;

  private static final UUID CASEGROUP_ID = UUID.fromString("3fc633af-d740-4a7b-8756-f747a02da73b");
  private static final UUID PARTY_ID = UUID.fromString("b2263303-a6b7-4562-aedf-eb97de4bc563");
  private static final UUID SURVEY_ID = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");

  @Test
  public void givenCaseGroupStatusWhenCaseGroupStatusTransitionedThenTransitionIsSaved()
      throws Exception {
    // Given
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(UUID.randomUUID())
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();

    CategoryDTO.CategoryName categoryName =
        CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED;
    given(caseGroupStatusTransitionManager.transition(caseGroup.getStatus(), categoryName))
        .willReturn(CaseGroupStatus.COMPLETE);

    // When
    caseGroupService.transitionCaseGroupStatus(caseGroup, categoryName, caseGroup.getPartyId());

    // Then
    verify(caseGroupRepo).saveAndFlush(caseGroup);
  }

  @Test
  public void givenCaseGroupStatusWhenCaseGroupStatusTransitionedThenTransitionIsAudited()
      throws Exception {
    // Given
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(UUID.randomUUID())
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();

    CategoryDTO.CategoryName categoryName =
        CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED;
    given(caseGroupStatusTransitionManager.transition(caseGroup.getStatus(), categoryName))
        .willReturn(CaseGroupStatus.COMPLETE);

    // When
    caseGroupService.transitionCaseGroupStatus(caseGroup, categoryName, caseGroup.getPartyId());

    // Then
    verify(caseGroupAuditService).updateAuditTable(caseGroup, caseGroup.getPartyId());
  }

  @Test
  public void testCaseGroupFindByIdSuccess() {
    // Given
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(CASEGROUP_ID)
            .collectionExerciseId(UUID.randomUUID())
            .partyId(UUID.randomUUID())
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    given(caseGroupRepo.findById(CASEGROUP_ID)).willReturn(caseGroup);

    // When
    CaseGroup response = caseGroupService.findCaseGroupById(CASEGROUP_ID);

    // Then
    verify(caseGroupRepo).findById(CASEGROUP_ID);
    assertEquals(response.getId(), CASEGROUP_ID);
    assertEquals(response.getCollectionExerciseId(), caseGroup.getCollectionExerciseId());
  }

  @Test
  public void testCaseGroupFindByPartyIdSuccess() {
    // Given
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(PARTY_ID)
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroup);
    given(caseGroupRepo.findByPartyId(PARTY_ID)).willReturn(caseGroupList);

    // When
    caseGroupService.findCaseGroupByPartyId(PARTY_ID);

    // Then
    verify(caseGroupRepo).findByPartyId(PARTY_ID);
  }

  @Test
  public void testCaseGroupFindBySurveyId() {
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(PARTY_ID)
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .surveyId(SURVEY_ID)
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroup);
    given(caseGroupRepo.findBySurveyId(SURVEY_ID)).willReturn(caseGroupList);

    caseGroupService.findCaseGroupBySurveyId(SURVEY_ID);

    verify(caseGroupRepo).findBySurveyId(SURVEY_ID);
  }

  @Test
  public void testCaseGroupFindByCollectionExerciseAndRuRefSuccess() {
    // Given
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(PARTY_ID)
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    given(
            caseGroupRepo.findCaseGroupByCollectionExerciseIdAndSampleUnitRef(
                caseGroup.getCollectionExerciseId(), caseGroup.getSampleUnitRef()))
        .willReturn(caseGroup);

    // When
    caseGroupService.findCaseGroupByCollectionExerciseIdAndRuRef(
        caseGroup.getCollectionExerciseId(), caseGroup.getSampleUnitRef());

    // Then
    verify(caseGroupRepo)
        .findCaseGroupByCollectionExerciseIdAndSampleUnitRef(
            caseGroup.getCollectionExerciseId(), caseGroup.getSampleUnitRef());
  }

  @Test
  public void CaseGroupFindForExecutedCollectionExercisesSuccess() throws Exception {
    // Given
    Case caze = FixtureHelper.loadClassFixtures(Case[].class).get(0);
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(PARTY_ID)
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    given(caseGroupRepo.findOne(any(int.class))).willReturn(caseGroup);
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroup);
    List<CollectionExerciseDTO> collectionExerciseDTOs =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    CollectionExerciseDTO collectionExercise = collectionExerciseDTOs.get(0);
    given(collectionExerciseSvcClient.getCollectionExercise(caseGroup.getCollectionExerciseId()))
        .willReturn(collectionExercise);
    given(collectionExerciseSvcClient.getCollectionExercises(collectionExercise.getSurveyId()))
        .willReturn(collectionExerciseDTOs);
    given(caseGroupRepo.retrieveByPartyIdInListOfCollEx(PARTY_ID, eq(anyList())))
        .willReturn(caseGroupList);

    // When
    caseGroupService.findCaseGroupsForExecutedCollectionExercises(caze);

    // Then
    verify(caseGroupRepo).findOne(caze.getCaseGroupFK());
  }

  @Test(expected = CTPException.class)
  public void
      CaseGroupFindForExecutedCollectionExercisesReturnNullCollectionExercisesThrowsException()
          throws Exception {
    // Given
    Case caze = FixtureHelper.loadClassFixtures(Case[].class).get(0);
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(PARTY_ID)
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    given(caseGroupRepo.findOne(any(int.class))).willReturn(caseGroup);
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroup);
    List<CollectionExerciseDTO> collectionExerciseDTOs =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    CollectionExerciseDTO collectionExercise = collectionExerciseDTOs.get(0);
    given(collectionExerciseSvcClient.getCollectionExercise(caseGroup.getCollectionExerciseId()))
        .willReturn(collectionExercise);
    given(collectionExerciseSvcClient.getCollectionExercises(collectionExercise.getSurveyId()))
        .willReturn(null);

    // When
    caseGroupService.findCaseGroupsForExecutedCollectionExercises(caze);

    // Then throw CTPException
  }

  @Test(expected = CTPException.class)
  public void
      CaseGroupFindForExecutedCollectionExerciseReturnNullCollectionExerciseThrowsException()
          throws Exception {
    // Given
    Case caze = FixtureHelper.loadClassFixtures(Case[].class).get(0);
    CaseGroup caseGroup =
        CaseGroup.builder()
            .id(UUID.randomUUID())
            .collectionExerciseId(UUID.randomUUID())
            .partyId(PARTY_ID)
            .sampleUnitRef("12345")
            .sampleUnitType("B")
            .status(CaseGroupStatus.NOTSTARTED)
            .build();
    given(caseGroupRepo.findOne(any(int.class))).willReturn(caseGroup);
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroup);
    List<CollectionExerciseDTO> collectionExerciseDTOs =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    CollectionExerciseDTO collectionExercise = collectionExerciseDTOs.get(0);
    given(collectionExerciseSvcClient.getCollectionExercise(caseGroup.getCollectionExerciseId()))
        .willReturn(null);

    // When
    caseGroupService.findCaseGroupsForExecutedCollectionExercises(caze);

    // Then throw CTPException
  }

  @Test(expected = CTPException.class)
  public void CaseGroupFindForExecutedFindCaseGroupReturnsNullThrowsException() throws Exception {
    // Given
    Case caze = FixtureHelper.loadClassFixtures(Case[].class).get(0);
    given(caseGroupRepo.findOne(any(int.class))).willReturn(null);

    // When
    caseGroupService.findCaseGroupsForExecutedCollectionExercises(caze);

    // Then throw CTPException
  }

  @Test(expected = CTPException.class)
  public void CaseGroupFindForExecutedCaseIsNullThrowsException() throws CTPException {
    // Given no case
    // When
    caseGroupService.findCaseGroupsForExecutedCollectionExercises(null);

    // Then throws CTPException
  }
}
