package uk.gov.ons.ctp.response.casesvc.service.impl;

import static org.junit.Assert.*;
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

import uk.gov.ons.ctp.response.casesvc.definition.CaseCreation;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseState;
import uk.gov.ons.ctp.response.casesvc.utility.Constants;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO.SampleUnitType;

@RunWith(MockitoJUnitRunner.class)
public class CaseCreationServiceTest {
	
	 @InjectMocks
	 private CaseServiceImpl caseService;

	 @Mock
	 private CaseGroupRepository caseGroupRepo;
	 
	 @Mock
	 private CaseRepository caseRepo;
	  
	 /**
	  * Create a Case and a Casegroup from the message that would be on the Case Delivery Queue
	  */
	@Test
	public void TestCreateCaseAndCasegroupFromMessage(){
		CaseCreation caseDTO = new CaseCreation();
		caseDTO.setActionPlanId("7bc5d41b-0549-40b3-ba76-42f6d4cf3991");
		caseDTO.setPartyId("7bc5d41b-0549-40b3-ba76-42f6d4cf3992");
		caseDTO.setCollectionExerciseId("7bc5d41b-0549-40b3-ba76-42f6d4cf3993");
		caseDTO.setCollectionInstrumentId("7bc5d41b-0549-40b3-ba76-42f6d4cf3994");
		caseDTO.setSampleUnitRef("str1234");
		caseDTO.setSampleUnitType("B");
		
		caseService.createInitialCase(caseDTO);
		
		ArgumentCaptor <CaseGroup> caseGroup = ArgumentCaptor.forClass(CaseGroup.class);

		verify(caseGroupRepo, times(1)).saveAndFlush(caseGroup.capture());
		
		List<CaseGroup> capturedCaseGroup = caseGroup.getAllValues();
		
		assertEquals(UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3992"), capturedCaseGroup.get(0).getPartyId());
		assertEquals(UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3993"), capturedCaseGroup.get(0).getCollectionExerciseId());
		assertEquals("str1234", capturedCaseGroup.get(0).getSampleUnitRef());
		assertEquals("B", capturedCaseGroup.get(0).getSampleUnitType());

		
		ArgumentCaptor <Case> caze = ArgumentCaptor.forClass(Case.class);

		verify(caseRepo, times(1)).saveAndFlush(caze.capture());
		
		List<Case> capturedCase = caze.getAllValues();
		
		assertEquals(UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3992"), capturedCase.get(0).getPartyId());
		assertEquals(SampleUnitType.B, capturedCase.get(0).getSampleUnitType());
		assertEquals(UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3991"), capturedCase.get(0).getActionPlanId());
		assertEquals(UUID.fromString("7bc5d41b-0549-40b3-ba76-42f6d4cf3994"), capturedCase.get(0).getCollectionInstrumentId());
		assertEquals(CaseState.SAMPLED_INIT, capturedCase.get(0).getState());
		assertEquals(Constants.SYSTEM, capturedCase.get(0).getCreatedBy());
    }
}
