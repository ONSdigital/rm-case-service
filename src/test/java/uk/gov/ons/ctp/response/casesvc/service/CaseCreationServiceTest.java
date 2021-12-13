package uk.gov.ons.ctp.response.casesvc.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnit;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitChildren;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;

/** Test Case created by Sample */
@RunWith(MockitoJUnitRunner.class)
public class CaseCreationServiceTest {

  @InjectMocks private CaseService caseService;

  @Mock private CaseRepository caseRepo;
  @Mock private CaseGroupRepository caseGroupRepo;
  @Mock private CaseEventRepository caseEventRepo;
  @Mock private CaseGroupService caseGroupService;
  @Mock private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Mock private AppConfig mockAppConfig;
  @Mock private StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseSvcStateTransitionManager;

  /**
   * Create a Case and a Casegroup from the message that would be on the Case Delivery Queue. No
   * child party.
   */
  @Test
  public void testCreateCaseAndCaseGroupWithoutChildFromMessage() throws Exception {

    SampleUnitParent sampleUnitParent = new SampleUnitParent();

    // Parent Only field
    sampleUnitParent.setCollectionExerciseId("14fb3e68-4dca-46db-bf49-04b84e07e77c");
    // Base sample unit data
    sampleUnitParent.setActiveEnrolment(false);
    sampleUnitParent.setCollectionInstrumentId("8bae64c5-a282-4e87-ae5d-cd4181ba6c73");
    sampleUnitParent.setPartyId("7bc5d41b-0549-40b3-ba76-42f6d4cf3992");
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());
    List<CollectionExerciseDTO> collectionExercises =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    when(collectionExerciseSvcClient.getCollectionExercise(any()))
        .thenReturn(collectionExercises.get(0));
    when(caseGroupService.isCaseGroupUnique(any(SampleUnitParent.class))).thenReturn(true);
    when(caseSvcStateTransitionManager.transition(
            CaseState.SAMPLED_INIT, CaseDTO.CaseEvent.ACTIVATED))
        .thenReturn(CaseState.ACTIONABLE);

    caseService.createInitialCase(sampleUnitParent);

    // Both CaseGroup and Case attributes created from parent sample unit
    ArgumentCaptor<CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);
    verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());
    CaseGroup capturedCaseGroup = caseGroup.getValue();

    assertEquals(UUID.class, capturedCaseGroup.getId().getClass());
    assertEquals(
        UUID.fromString(sampleUnitParent.getCollectionExerciseId()),
        capturedCaseGroup.getCollectionExerciseId());
    assertEquals(UUID.fromString(sampleUnitParent.getPartyId()), capturedCaseGroup.getPartyId());
    assertEquals(sampleUnitParent.getSampleUnitRef(), capturedCaseGroup.getSampleUnitRef());
    assertEquals(sampleUnitParent.getSampleUnitType(), capturedCaseGroup.getSampleUnitType());
    assertEquals(CaseGroupStatus.NOTSTARTED, capturedCaseGroup.getStatus());

    ArgumentCaptor<Case> caze = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(2)).saveAndFlush(caze.capture());
    Case capturedCase = caze.getValue();

    assertEquals(UUID.class, capturedCase.getId().getClass());
    assertEquals(new Integer(capturedCaseGroup.getCaseGroupPK()), capturedCase.getCaseGroupFK());
    assertEquals(capturedCaseGroup.getId(), capturedCase.getCaseGroupId());
    assertEquals(CaseState.ACTIONABLE, capturedCase.getState());
    assertEquals(
        SampleUnitDTO.SampleUnitType.valueOf(sampleUnitParent.getSampleUnitType()),
        capturedCase.getSampleUnitType());
    assertEquals(UUID.fromString(sampleUnitParent.getPartyId()), capturedCase.getPartyId());
    assertEquals(
        UUID.fromString(sampleUnitParent.getCollectionInstrumentId()),
        capturedCase.getCollectionInstrumentId());
    assertEquals(sampleUnitParent.isActiveEnrolment(), capturedCase.isActiveEnrolment());
  }

  /**
   * Create a Case and a Casegroup from the message that would be on the Case Delivery Queue. Child
   * party present.
   */
  @Test
  public void testCreateCaseAndCaseGroupWithChildFromMessage() throws Exception {

    SampleUnit sampleUnitChild = new SampleUnit();
    // Base sample unit data for child
    sampleUnitChild.setActiveEnrolment(false);
    sampleUnitChild.setCollectionInstrumentId("ed0015f0-2e7f-4cf3-ba6f-a752aebaf8a7");
    sampleUnitChild.setPartyId("73528fd7-ef04-4697-a94c-54edf3e73282");
    sampleUnitChild.setSampleUnitRef("str1235");
    sampleUnitChild.setSampleUnitType("BI");
    sampleUnitChild.setId(UUID.randomUUID().toString());
    SampleUnitChildren sampleUnitChildren =
        new SampleUnitChildren(new ArrayList<>(Collections.singletonList(sampleUnitChild)));

    SampleUnitParent sampleUnitParent = new SampleUnitParent();
    // Parent Only field
    sampleUnitParent.setSampleUnitChildren(sampleUnitChildren);
    sampleUnitParent.setCollectionExerciseId("14fb3e68-4dca-46db-bf49-04b84e07e77c");
    // Base sample unit data for parent
    sampleUnitParent.setActiveEnrolment(true);
    sampleUnitParent.setCollectionInstrumentId("8bae64c5-a282-4e87-ae5d-cd4181ba6c73");
    sampleUnitParent.setPartyId("7bc5d41b-0549-40b3-ba76-42f6d4cf3992");
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());

    List<CollectionExerciseDTO> collectionExercises =
        FixtureHelper.loadClassFixtures(CollectionExerciseDTO[].class);
    when(collectionExerciseSvcClient.getCollectionExercise(any()))
        .thenReturn(collectionExercises.get(0));
    when(caseGroupService.isCaseGroupUnique(any(SampleUnitParent.class))).thenReturn(true);
    when(caseSvcStateTransitionManager.transition(
            CaseState.SAMPLED_INIT, CaseDTO.CaseEvent.ACTIVATED))
        .thenReturn(CaseState.ACTIONABLE);
    when(caseSvcStateTransitionManager.transition(CaseState.INACTIONABLE, null))
        .thenReturn(CaseState.INACTIONABLE);
    caseService.createInitialCase(sampleUnitParent);

    // CaseGroup attributes from parent data, case from child sample unit
    ArgumentCaptor<CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);
    verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());
    CaseGroup capturedCaseGroup = caseGroup.getValue();

    assertEquals(UUID.class, capturedCaseGroup.getId().getClass());
    assertEquals(
        UUID.fromString(sampleUnitParent.getCollectionExerciseId()),
        capturedCaseGroup.getCollectionExerciseId());
    assertEquals(UUID.fromString(sampleUnitParent.getPartyId()), capturedCaseGroup.getPartyId());
    assertEquals(sampleUnitParent.getSampleUnitRef(), capturedCaseGroup.getSampleUnitRef());
    assertEquals(sampleUnitParent.getSampleUnitType(), capturedCaseGroup.getSampleUnitType());
    assertEquals(CaseGroupStatus.NOTSTARTED, capturedCaseGroup.getStatus());

    ArgumentCaptor<Case> caze = ArgumentCaptor.forClass(Case.class);
    verify(caseRepo, times(4)).saveAndFlush(caze.capture());
    List<Case> capturedCases = caze.getAllValues();

    Case childCase = capturedCases.get(0);
    assertEquals(UUID.class, childCase.getId().getClass());
    assertEquals(new Integer(capturedCaseGroup.getCaseGroupPK()), childCase.getCaseGroupFK());
    assertEquals(capturedCaseGroup.getId(), childCase.getCaseGroupId());
    assertEquals(CaseState.ACTIONABLE, childCase.getState());
    assertEquals(
        SampleUnitDTO.SampleUnitType.valueOf(sampleUnitChild.getSampleUnitType()),
        childCase.getSampleUnitType());
    assertEquals(UUID.fromString(sampleUnitChild.getPartyId()), childCase.getPartyId());
    assertEquals(
        UUID.fromString(sampleUnitChild.getCollectionInstrumentId()),
        childCase.getCollectionInstrumentId());
    assertEquals(sampleUnitChild.isActiveEnrolment(), childCase.isActiveEnrolment());

    Case parentCase = capturedCases.get(2);
    assertEquals(UUID.class, parentCase.getId().getClass());
    assertEquals(new Integer(capturedCaseGroup.getCaseGroupPK()), parentCase.getCaseGroupFK());
    assertEquals(capturedCaseGroup.getId(), parentCase.getCaseGroupId());
    assertEquals(CaseState.INACTIONABLE, parentCase.getState());
    assertEquals(
        SampleUnitDTO.SampleUnitType.valueOf(sampleUnitParent.getSampleUnitType()),
        parentCase.getSampleUnitType());
    assertEquals(UUID.fromString(sampleUnitParent.getPartyId()), parentCase.getPartyId());
    assertEquals(
        UUID.fromString(sampleUnitParent.getCollectionInstrumentId()),
        parentCase.getCollectionInstrumentId());
    assertEquals(sampleUnitParent.isActiveEnrolment(), parentCase.isActiveEnrolment());
  }

  /**
   * Create a Case and a Casegroup from the message that would be on the Case Delivery Queue. Child
   * party present.
   */
  @Test
  public void testCreateCaseAndCaseGroupDoesNothingIfDuplicate() throws Exception {

    SampleUnit sampleUnitChild = new SampleUnit();
    // Base sample unit data for child
    sampleUnitChild.setActiveEnrolment(false);
    sampleUnitChild.setCollectionInstrumentId("ed0015f0-2e7f-4cf3-ba6f-a752aebaf8a7");
    sampleUnitChild.setPartyId("73528fd7-ef04-4697-a94c-54edf3e73282");
    sampleUnitChild.setSampleUnitRef("str1235");
    sampleUnitChild.setSampleUnitType("BI");
    sampleUnitChild.setId(UUID.randomUUID().toString());
    SampleUnitChildren sampleUnitChildren =
        new SampleUnitChildren(new ArrayList<>(Collections.singletonList(sampleUnitChild)));

    SampleUnitParent sampleUnitParent = new SampleUnitParent();
    // Parent Only field
    sampleUnitParent.setSampleUnitChildren(sampleUnitChildren);
    sampleUnitParent.setCollectionExerciseId("14fb3e68-4dca-46db-bf49-04b84e07e77c");
    // Base sample unit data for parent
    sampleUnitParent.setActiveEnrolment(true);
    sampleUnitParent.setCollectionInstrumentId("8bae64c5-a282-4e87-ae5d-cd4181ba6c73");
    sampleUnitParent.setPartyId("7bc5d41b-0549-40b3-ba76-42f6d4cf3992");
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());

    // case group already exists
    when(caseGroupService.isCaseGroupUnique(any(SampleUnitParent.class))).thenReturn(false);

    caseService.createInitialCase(sampleUnitParent);

    // then nothing should be saved
    verify(caseGroupRepo, times(0)).saveAndFlush(any(CaseGroup.class));
    verify(caseRepo, times(0)).saveAndFlush(any(Case.class));
  }
}
