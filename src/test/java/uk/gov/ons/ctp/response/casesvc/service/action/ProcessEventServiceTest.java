package uk.gov.ons.ctp.response.casesvc.service.action;

import static org.mockito.Mockito.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.scheduling.annotation.AsyncResult;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionEventRequestRepository;
import uk.gov.ons.ctp.response.casesvc.service.action.email.ProcessEmailActionService;
import uk.gov.ons.ctp.response.casesvc.service.action.letter.ProcessEventServiceTestData;
import uk.gov.ons.ctp.response.casesvc.service.action.letter.ProcessLetterActionService;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventServiceTest {
  @Mock private CollectionExerciseSvcClient collectionExerciseClientService;
  @Spy private CaseActionEventRequestRepository caseActionEventRequestRepository;
  @Mock private ProcessLetterActionService processLetterActionService;
  @Mock private ProcessEmailActionService emailEventService;
  @InjectMocks private ProcessCaseActionEventService processEventService;

  private ProcessEventServiceTestData testData = new ProcessEventServiceTestData();

  @Test
  public void testProcessEventsSuccess() throws ExecutionException, InterruptedException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    Mockito.when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    Mockito.when(emailEventService.processEmailService(any(), any()))
        .thenReturn(new AsyncResult<>(Boolean.TRUE));
    Mockito.when(processLetterActionService.processLetterService(any(), any()))
        .thenReturn(new AsyncResult<>(Boolean.TRUE));
    processEventService.processEvents(collectionExerciseId, "go_live");
    verify(caseActionEventRequestRepository, atLeast(2)).save(any());
    verify(caseActionEventRequestRepository, atMost(2)).save(any());
  }

  @Test
  public void testProcessEventsFailure() throws ExecutionException, InterruptedException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    Mockito.when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    Mockito.when(emailEventService.processEmailService(any(), any()))
        .thenReturn(new AsyncResult<>(Boolean.FALSE));
    Mockito.when(processLetterActionService.processLetterService(any(), any()))
        .thenReturn(new AsyncResult<>(Boolean.TRUE));
    processEventService.processEvents(collectionExerciseId, "go_live");
    verify(caseActionEventRequestRepository, atLeast(2)).save(any());
    verify(caseActionEventRequestRepository, atMost(2)).save(any());
  }
}
