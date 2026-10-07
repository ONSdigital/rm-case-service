package uk.gov.ons.ctp.response.casesvc.service;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.data.domain.PageRequest;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CollectionExerciseCaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.ReportingUnitCaseDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseCaseGroupDTO;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;

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
  private static final UUID CASE_ID = UUID.fromString("a3a966ab-535f-4572-b4c3-fab2bb1ed5fc");
  private static final UUID COLLECTION_EXERCISE_UUID =
      UUID.fromString("a3a966ab-535f-4572-b4c3-fab2bb1ed5fc");
  private static final UUID PARTY_ID = UUID.fromString("b2263303-a6b7-4562-aedf-eb97de4bc563");
  private static final UUID SURVEY_ID = UUID.fromString("cb8accda-6118-4d3b-85a3-149e28960c54");
  private static final String IAC = "xyts9jxk2gbl";

  @Test
  public void testCaseGroupCorrectlyTransitionsToNewStatus() throws Exception {
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
    assertNotNull(caseGroup.getStatusChangeTimestamp());
    assertEquals(caseGroup.getStatus(), CaseGroupStatus.COMPLETE);
    verify(caseGroupRepo).saveAndFlush(caseGroup);
  }

  @Test
  public void testCaseGroupStatusChangeIsCorrectlyAudited() throws Exception {
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
  public void testGetReportingUnitsByPartyAndSurveyId() throws Exception {
    List<ReportingUnitCaseDTO> reportingUnits = new ArrayList<>();
    ReportingUnitCaseDTO reportingUnit =
        new ReportingUnitCaseDTO(
            COLLECTION_EXERCISE_UUID,
            CaseGroupStatus.COMPLETE,
            CASE_ID,
            new Timestamp(System.currentTimeMillis()),
            IAC);
    reportingUnits.add(reportingUnit);

    given(
            caseGroupRepo.findReportingUnitsByPartyAndSurveyId(
                PageRequest.of(0, 10), PARTY_ID, SURVEY_ID))
        .willReturn(reportingUnits);

    List<ReportingUnitCaseDTO> response =
        caseGroupService.getReportingUnitsByPartyAndSurveyId(PARTY_ID, SURVEY_ID, 10);

    assertEquals(response, reportingUnits);
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
    given(caseGroupRepo.findById(any(int.class))).willReturn(Optional.of(caseGroup));
    List<CaseGroup> caseGroupList = Collections.singletonList(caseGroup);
    List<CollectionExerciseDTO> collectionExerciseDTOs =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    CollectionExerciseDTO collectionExercise = collectionExerciseDTOs.get(0);
    given(collectionExerciseSvcClient.getCollectionExercise(caseGroup.getCollectionExerciseId()))
        .willReturn(collectionExercise);
    given(collectionExerciseSvcClient.getCollectionExercises(collectionExercise.getSurveyId()))
        .willReturn(collectionExerciseDTOs);

    // When
    caseGroupService.findCaseGroupsForExecutedCollectionExercises(caze);

    // Then
    verify(caseGroupRepo).findById(caze.getCaseGroupFK());
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
    given(caseGroupRepo.findById(any(int.class))).willReturn(Optional.of(caseGroup));
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
    given(caseGroupRepo.findById(any(int.class))).willReturn(Optional.of(caseGroup));
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
    given(caseGroupRepo.findById(any(int.class))).willReturn(Optional.ofNullable(null));

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

  @Test
  public void getCollectionExercisesByPartyId() {
    UUID partyId = UUID.randomUUID();
    UUID collectionExerciseId1 = UUID.randomUUID();
    UUID collectionExerciseId2 = UUID.randomUUID();

    CollectionExerciseCaseGroup caseGroup1 = mock(CollectionExerciseCaseGroup.class);
    when(caseGroup1.getCollectionExerciseId()).thenReturn(collectionExerciseId1);
    when(caseGroup1.getStatus()).thenReturn("INPROGRESS");

    CollectionExerciseCaseGroup caseGroup2 = mock(CollectionExerciseCaseGroup.class);
    when(caseGroup2.getCollectionExerciseId()).thenReturn(collectionExerciseId2);
    when(caseGroup2.getStatus()).thenReturn("COMPLETE");

    when(caseGroupRepo.findCollectionExerciseDetailsByPartyId(partyId))
        .thenReturn(Arrays.asList(caseGroup1, caseGroup2));

    CollectionExerciseDTO collectionExercise1 = new CollectionExerciseDTO();
    collectionExercise1.setId(collectionExerciseId1);

    CollectionExerciseDTO collectionExercise2 = new CollectionExerciseDTO();
    collectionExercise2.setId(collectionExerciseId2);

    when(collectionExerciseSvcClient.getCollectionExercises(
            Arrays.asList(collectionExerciseId1, collectionExerciseId2), false))
        .thenReturn(Arrays.asList(collectionExercise1, collectionExercise2));

    List<CollectionExerciseCaseGroupDTO> result =
        caseGroupService.getCollectionExercisesByPartyId(partyId, null, false);

    assertEquals(2, result.size());

    assertEquals(collectionExercise1, result.get(0).getCollectionExercise());
    assertEquals("INPROGRESS", result.get(0).getCaseGroupStatus());

    assertEquals(collectionExercise2, result.get(1).getCollectionExercise());
    assertEquals("COMPLETE", result.get(1).getCaseGroupStatus());

    verify(collectionExerciseSvcClient)
        .getCollectionExercises(Arrays.asList(collectionExerciseId1, collectionExerciseId2), false);
  }

  @Test
  public void getCollectionExercisesByPartyIdReturnsEmptyList() {
    UUID partyId = UUID.randomUUID();

    when(caseGroupRepo.findCollectionExerciseDetailsByPartyId(partyId))
        .thenReturn(Collections.emptyList());

    List<CollectionExerciseCaseGroupDTO> result =
        caseGroupService.getCollectionExercisesByPartyId(partyId, null, false);

    assertEquals(0, result.size());

    verify(collectionExerciseSvcClient, never()).getCollectionExercises(anyList(), anyBoolean());
  }

  @Test
  public void getCollectionExercisesByPartyIdFiltersBySurveyId() {
    UUID partyId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID otherSurveyId = UUID.randomUUID();

    UUID collectionExerciseId = UUID.randomUUID();

    CollectionExerciseCaseGroup matchingCaseGroup = mock(CollectionExerciseCaseGroup.class);
    when(matchingCaseGroup.getSurveyId()).thenReturn(surveyId);
    when(matchingCaseGroup.getCollectionExerciseId()).thenReturn(collectionExerciseId);
    when(matchingCaseGroup.getStatus()).thenReturn("INPROGRESS");

    CollectionExerciseCaseGroup nonMatchingCaseGroup = mock(CollectionExerciseCaseGroup.class);
    when(nonMatchingCaseGroup.getSurveyId()).thenReturn(otherSurveyId);

    when(caseGroupRepo.findCollectionExerciseDetailsByPartyId(partyId))
        .thenReturn(Arrays.asList(matchingCaseGroup, nonMatchingCaseGroup));

    CollectionExerciseDTO collectionExercise = new CollectionExerciseDTO();
    collectionExercise.setId(collectionExerciseId);

    when(collectionExerciseSvcClient.getCollectionExercises(
            Collections.singletonList(collectionExerciseId), false))
        .thenReturn(Collections.singletonList(collectionExercise));

    List<CollectionExerciseCaseGroupDTO> result =
        caseGroupService.getCollectionExercisesByPartyId(partyId, surveyId, false);

    assertEquals(1, result.size());
    assertEquals(collectionExercise, result.get(0).getCollectionExercise());
    assertEquals("INPROGRESS", result.get(0).getCaseGroupStatus());

    verify(collectionExerciseSvcClient)
        .getCollectionExercises(Collections.singletonList(collectionExerciseId), false);
  }

  @Test
  public void getCollectionExercisesByPartyIdPassesSurveyLatest() {
    UUID partyId = UUID.randomUUID();
    UUID collectionExerciseId = UUID.randomUUID();

    CollectionExerciseCaseGroup caseGroup = mock(CollectionExerciseCaseGroup.class);
    when(caseGroup.getCollectionExerciseId()).thenReturn(collectionExerciseId);
    when(caseGroup.getStatus()).thenReturn("INPROGRESS");

    when(caseGroupRepo.findCollectionExerciseDetailsByPartyId(partyId))
        .thenReturn(Collections.singletonList(caseGroup));

    CollectionExerciseDTO collectionExercise = new CollectionExerciseDTO();
    collectionExercise.setId(collectionExerciseId);

    when(collectionExerciseSvcClient.getCollectionExercises(
            Collections.singletonList(collectionExerciseId), true))
        .thenReturn(Collections.singletonList(collectionExercise));

    List<CollectionExerciseCaseGroupDTO> result =
        caseGroupService.getCollectionExercisesByPartyId(partyId, null, true);

    assertEquals(1, result.size());
    assertEquals(collectionExercise, result.get(0).getCollectionExercise());
    assertEquals("INPROGRESS", result.get(0).getCaseGroupStatus());

    verify(collectionExerciseSvcClient)
        .getCollectionExercises(Collections.singletonList(collectionExerciseId), true);
  }
}
