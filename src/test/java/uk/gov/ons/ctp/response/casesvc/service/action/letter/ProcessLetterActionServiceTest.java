package uk.gov.ons.ctp.response.casesvc.service.action.letter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate.Handler;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseGroupRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseAction;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionParty;
import uk.gov.ons.ctp.response.casesvc.service.action.ActionTemplateService;
import uk.gov.ons.ctp.response.casesvc.service.action.ProcessEventServiceTestData;
import uk.gov.ons.ctp.response.casesvc.service.action.common.ActionService;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@RunWith(MockitoJUnitRunner.class)
public class ProcessLetterActionServiceTest {
  @Mock private ActionTemplateService actionTemplateService;
  @Mock private CaseGroupRepository caseGroupRepository;
  @Mock private NotifyLetterService letterService;
  @Mock private ActionService actionService;
  @InjectMocks private ProcessLetterActionService processLetterActionService;

  private ProcessEventServiceTestData testData = new ProcessEventServiceTestData();

  @Test
  public void noLetterToBeProcessed() throws ExecutionException, InterruptedException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    CollectionExerciseDTO collectionExerciseDTO =
        testData.setupCollectionExerciseDTO(collectionExerciseId, surveyId, "400000005", "test");
    Future<Boolean> future =
        processLetterActionService.processLetterService(
            collectionExerciseDTO, "mps", Instant.now());
    Assert.assertEquals(future.get(), true);
    verify(letterService, never()).processPrintFile(any(), any());
  }

  @Test
  public void letterToBeProcessed() throws ExecutionException, InterruptedException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    UUID caseId = UUID.randomUUID();
    UUID partyId = UUID.randomUUID();
    UUID sampleUnitId = UUID.randomUUID();
    UUID respondentId = UUID.randomUUID();
    CollectionExerciseDTO collectionExerciseDTO =
        testData.setupCollectionExerciseDTO(collectionExerciseId, surveyId, "400000005", "test");
    SurveyDTO surveyDTO = testData.setupSurveyDTO(surveyId, "test", "400000005", "test");
    Mockito.when(actionService.getSurvey(surveyId.toString())).thenReturn(surveyDTO);
    List<CaseAction> actionCases = new ArrayList<>();
    actionCases.add(
        testData.setupActionCase(
            caseId,
            false,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));
    when(caseGroupRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, false))
        .thenReturn(actionCases);

    when(actionTemplateService.mapEventTagToTemplate("mps", false))
        .thenReturn(testData.setupActionTemplate("BSNE", Handler.EMAIL, "mps"));
    when(actionService.isActionable(any(), any(), any())).thenReturn(true);
    PartyDTO parentParty =
        testData.setupBusinessParty("1", "YY", "test", "test@test.com", respondentId.toString());
    PartyDTO childParty =
        testData.setupRespondentParty("test", "test", "test@test.com", respondentId.toString());
    when(letterService.processPrintFile(any(), any())).thenReturn(true);
    CaseActionParty caseActionParty = new CaseActionParty(parentParty, List.of(childParty));
    when(actionService.setParties(any(), any())).thenReturn(caseActionParty);
    Future<Boolean> future =
        processLetterActionService.processLetterService(
            collectionExerciseDTO, "mps", Instant.now());
    Assert.assertEquals(future.get(), true);
    verify(letterService, times(1)).processPrintFile(any(), any());
    verify(actionService, times(1))
        .createCaseActionEvent(any(), any(), any(), any(), any(), any(), any());
  }
}
