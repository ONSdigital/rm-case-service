package uk.gov.ons.ctp.response.casesvc.service.action;

import static org.mockito.Mockito.*;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionEventRequestRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionEvent;
import uk.gov.ons.ctp.response.casesvc.service.action.email.ProcessEmailActionService;
import uk.gov.ons.ctp.response.casesvc.service.action.letter.ProcessLetterActionService;

@RunWith(MockitoJUnitRunner.class)
public class ProcessEventServiceTest {
  @Mock private CollectionExerciseSvcClient collectionExerciseClientService;
  @Spy private CaseActionEventRequestRepository caseActionEventRequestRepository;
  @Mock private ProcessLetterActionService processLetterActionService;
  @Mock private ProcessEmailActionService emailEventService;
  @Mock private ObjectMapper objectMapper;

  @Mock
  private CaseSvcApplication.PubSubOutboundCollectionExerciseEventStatusGateway
      collectionExerciseEventStatusUpdate;

  @InjectMocks private ProcessCaseActionEventService processEventService;

  private ProcessEventServiceTestData testData = new ProcessEventServiceTestData();

  @Test
  public void testProcessEventsSuccess()
      throws ExecutionException, InterruptedException, JsonProcessingException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    Mockito.when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    Mockito.when(emailEventService.processEmailService(any(), any(), any()))
        .thenReturn(new AsyncResult<>(true));
    Mockito.when(processLetterActionService.processLetterService(any(), any(), any()))
        .thenReturn(new AsyncResult<>(true));
    CaseActionEvent actionEvent = new CaseActionEvent();
    actionEvent.setCollectionExerciseID(collectionExerciseId);
    actionEvent.setTag(CaseActionEvent.EventTag.go_live);
    processEventService.processEvents(actionEvent);
    verify(caseActionEventRequestRepository, times(2)).save(any());
    verify(caseActionEventRequestRepository, times(2)).save(any());
    verify(collectionExerciseEventStatusUpdate, times(1)).sendToPubSub(any());
  }

  @Test
  public void testProcessEventsFailure()
      throws ExecutionException, InterruptedException, JsonProcessingException {
    UUID collectionExerciseId = UUID.randomUUID();
    UUID surveyId = UUID.randomUUID();
    Mockito.when(collectionExerciseClientService.getCollectionExercise(collectionExerciseId))
        .thenReturn(
            testData.setupCollectionExerciseDTO(
                collectionExerciseId, surveyId, "400000005", "test"));
    Mockito.when(emailEventService.processEmailService(any(), any(), any()))
        .thenReturn(new AsyncResult<>(false));
    Mockito.when(processLetterActionService.processLetterService(any(), any(), any()))
        .thenReturn(new AsyncResult<>(true));
    CaseActionEvent actionEvent = new CaseActionEvent();
    actionEvent.setCollectionExerciseID(collectionExerciseId);
    actionEvent.setTag(CaseActionEvent.EventTag.go_live);
    processEventService.processEvents(actionEvent);
    verify(caseActionEventRequestRepository, times(2)).save(any());
    verify(caseActionEventRequestRepository, times(2)).save(any());
    verify(collectionExerciseEventStatusUpdate, times(1)).sendToPubSub(any());
  }
}
