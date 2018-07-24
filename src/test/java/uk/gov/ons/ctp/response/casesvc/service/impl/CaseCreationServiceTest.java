package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import java.util.UUID;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseEventRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

/** Test Case created by Sample */
@RunWith(MockitoJUnitRunner.class)
public class CaseCreationServiceTest {

  @InjectMocks private CaseServiceImpl caseService;

  @Mock private CaseGroupRepository caseGroupRepo;

  @Mock private CaseRepository caseRepo;

  @Mock private CaseEventRepository caseEventRepo;

  /**
   * Create a Case and a Casegroup from the message that would be on the Case Delivery Queue. No
   * child party.
   */
  @Test
  public void testCreateCaseAndCaseGroupWithoutChildFromMessage() {

    SampleUnitParent sampleUnitParent = new SampleUnitParent();

    // Parent Only field
    sampleUnitParent.setCollectionExerciseId("14fb3e68-4dca-46db-bf49-04b84e07e77c");
    // Parent has actionplanId or child sample unit
    sampleUnitParent.setActionPlanId("7bc5d41b-0549-40b3-ba76-42f6d4cf3991");
    // Base sample unit data
    sampleUnitParent.setCollectionInstrumentId("8bae64c5-a282-4e87-ae5d-cd4181ba6c73");
    sampleUnitParent.setPartyId("7bc5d41b-0549-40b3-ba76-42f6d4cf3992");
    sampleUnitParent.setSampleUnitRef("str1234");
    sampleUnitParent.setSampleUnitType("B");
    sampleUnitParent.setId(UUID.randomUUID().toString());

    caseService.createInitialCase(sampleUnitParent);

    // Both CaseGroup and Case attributes created from parent sample unit
    ArgumentCaptor<CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);
    verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());

    List<CaseGroup> capturedCaseGroup = caseGroup.getAllValues();

    assertEquals(UUID.class, capturedCaseGroup.get(0).getId().getClass());
    assertEquals(
        UUID.fromString(sampleUnitParent.getCollectionExerciseId()),
        capturedCaseGroup.get(0).getCollectionExerciseId());
    assertEquals(
        UUID.fromString(sampleUnitParent.getPartyId()), capturedCaseGroup.get(0).getPartyId());
    assertEquals(sampleUnitParent.getSampleUnitRef(), capturedCaseGroup.get(0).getSampleUnitRef());
    assertEquals(
        sampleUnitParent.getSampleUnitType(), capturedCaseGroup.get(0).getSampleUnitType());
    assertEquals(CaseGroupStatus.NOTSTARTED, capturedCaseGroup.get(0).getStatus());

    ArgumentCaptor<Case> caze = ArgumentCaptor.forClass(Case.class);

    verify(caseRepo, times(1)).saveAndFlush(caze.capture());

    List<Case> capturedCase = caze.getAllValues();

    assertEquals(UUID.class, capturedCase.get(0).getId().getClass());
    assertEquals(
        new Integer(capturedCaseGroup.get(0).getCaseGroupPK()),
        capturedCase.get(0).getCaseGroupFK());
    assertEquals(capturedCaseGroup.get(0).getId(), capturedCase.get(0).getCaseGroupId());
    assertEquals(CaseState.SAMPLED_INIT, capturedCase.get(0).getState());
    assertEquals(
        SampleUnitDTO.SampleUnitType.valueOf(sampleUnitParent.getSampleUnitType()),
        capturedCase.get(0).getSampleUnitType());
    assertEquals(UUID.fromString(sampleUnitParent.getPartyId()), capturedCase.get(0).getPartyId());
    assertEquals(
        UUID.fromString(sampleUnitParent.getCollectionInstrumentId()),
        capturedCase.get(0).getCollectionInstrumentId());
    assertEquals(
        UUID.fromString(sampleUnitParent.getActionPlanId()), capturedCase.get(0).getActionPlanId());
  }
}
