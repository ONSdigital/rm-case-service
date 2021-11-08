package uk.gov.ons.ctp.response.casesvc.service.action.email;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseAction;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate.Handler;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionParty;
import uk.gov.ons.ctp.response.casesvc.service.action.ActionTemplateService;
import uk.gov.ons.ctp.response.casesvc.service.action.ProcessEventServiceTestData;
import uk.gov.ons.ctp.response.casesvc.service.action.common.ActionService;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;
import uk.gov.ons.ctp.response.lib.party.representation.PartyDTO;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;
import uk.gov.ons.ctp.response.lib.survey.representation.SurveyDTO;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEmailActionServiceTest {
  @Mock private ActionTemplateService actionTemplateService;
  @Mock private CaseActionRepository caseActionRepository;
  @Mock private NotifyEmailService emailService;
  @Mock private ActionService actionService;
  @InjectMocks private ProcessEmailActionService processEmailActionService;

  private ProcessEventServiceTestData testData = new ProcessEventServiceTestData();

  @Test
  public void noEmailToBeProcessed() throws ExecutionException, InterruptedException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    CollectionExerciseDTO collectionExerciseDTO =
        testData.setupCollectionExerciseDTO(collectionExerciseId, surveyId, "400000005", "test");
    Future<Boolean> future =
        processEmailActionService.processEmailService(collectionExerciseDTO, "mps", Instant.now());
    Assert.assertEquals(future.get(), true);
    verify(emailService, never()).processEmail(any());
  }

  @Test
  public void emailToBeProcessed() throws ExecutionException, InterruptedException {
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
            true,
            collectionExerciseId,
            partyId,
            SampleUnitDTO.SampleUnitType.B.toString(),
            sampleUnitId,
            "400000005",
            "oiauen"));
    when(caseActionRepository.findByCollectionExerciseIdAndActiveEnrolment(
            collectionExerciseId, true))
        .thenReturn(actionCases);

    when(actionTemplateService.mapEventTagToTemplate("go_live", true))
        .thenReturn(testData.setupActionTemplate("BSNE", Handler.EMAIL, "go_live"));
    when(actionService.isActionable(any(), any(), any())).thenReturn(true);
    PartyDTO parentParty =
        testData.setupBusinessParty("1", "YY", "test", "test@test.com", respondentId.toString());
    PartyDTO childParty =
        testData.setupRespondentParty("test", "test", "test@test.com", respondentId.toString());
    CaseActionParty caseActionParty = new CaseActionParty(parentParty, List.of(childParty));
    when(actionService.setParties(any(), any())).thenReturn(caseActionParty);
    Future<Boolean> future =
        processEmailActionService.processEmailService(
            collectionExerciseDTO, "go_live", Instant.now());
    Assert.assertEquals(future.get(), true);
    verify(emailService, times(1)).processEmail(any());
    verify(actionService, times(1))
        .createCaseActionEvent(any(), any(), any(), any(), any(), any(), any());
  }
}