package uk.gov.ons.ctp.response.casesvc.service.action;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication.PubSubOutboundCollectionExerciseEventStatusGateway;
import uk.gov.ons.ctp.response.casesvc.client.CollectionExerciseSvcClient;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionEventRequest;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionEventRequest.ActionEventRequestStatus;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionEventRequestRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionEvent;
import uk.gov.ons.ctp.response.casesvc.service.action.email.ProcessEmailActionService;
import uk.gov.ons.ctp.response.casesvc.service.action.letter.ProcessLetterActionService;
import uk.gov.ons.ctp.response.lib.collection.exercise.CollectionExerciseDTO;

@Service
public class ProcessCaseActionEventService {
  private static final Logger log = LoggerFactory.getLogger(ProcessCaseActionEventService.class);

  @Autowired private CollectionExerciseSvcClient collectionExerciseSvcClient;
  @Autowired private ProcessEmailActionService processEmailService;
  @Autowired private ProcessLetterActionService processLetterService;
  @Autowired private CaseActionEventRequestRepository actionEventRequestRepository;
  @Autowired private ObjectMapper objectMapper;

  @Autowired
  private PubSubOutboundCollectionExerciseEventStatusGateway collectionExerciseEventStatusUpdate;

  /**
   * Processes Events. This method takes two attributes collection Exercise Id and Event Tag.
   * GetsCollectionExerciseDTO and SurveyDTO from @param collectionExerciseId Gets all email cases
   * against collectionExerciseID (isActiveEnrolment == true) Maps email cases to ActionTemplate
   * processes email cases if actionable Gets all letter cases against collectionExerciseID
   * (isActiveEnrolment == false) Maps letter cases to ActionTemplate processes letter cases if
   * actionable
   *
   * @param event - Event information is passed
   */
  @Async
  public void processEvents(CaseActionEvent event)
      throws ExecutionException, InterruptedException, JsonProcessingException {
    UUID collectionExerciseId = event.getCollectionExerciseID();
    String eventTag = event.getTag().toString();
    log.with("collectionExerciseId", collectionExerciseId)
        .with("eventTag", eventTag)
        .info("Started processing");
    Instant instant = Instant.now();
    CollectionExerciseDTO collectionExercise = getCollectionExercise(collectionExerciseId);
    // Check if ProcessActionEventRequest table for existing status else add it.
    List<CaseActionEventRequest> existingRequests =
        actionEventRequestRepository.findByCollectionExerciseIdAndEventTag(
            collectionExerciseId, eventTag);
    if (!existingRequests.isEmpty()) {
      log.with("collectionExerciseId", collectionExerciseId)
          .with("eventTag", eventTag)
          .warn("Aborting processing event as an existing request is in progress.");
      return;
    }
    CaseActionEventRequest newRequest =
        CaseActionEventRequest.builder()
            .collectionExerciseId(collectionExerciseId)
            .eventTag(eventTag)
            .requestedTimestamp(Timestamp.from(instant))
            .status(ActionEventRequestStatus.INPROGRESS)
            .build();
    actionEventRequestRepository.save(newRequest);
    log.with("collectionExerciseId", collectionExerciseId)
        .with("eventTag", eventTag)
        .info("Requested event is now in progress.");
    log.with("collectionExerciseId", collectionExerciseId)
        .with("eventTag", eventTag)
        .debug("Requested event will now trigger email processing asynchronously.");
    Future<Boolean> asyncEmailCall =
        processEmailService.processEmailService(collectionExercise, eventTag, instant);
    log.with("collectionExerciseId", collectionExerciseId)
        .with("eventTag", eventTag)
        .debug("Requested event will now trigger letter processing asynchronously.");
    Future<Boolean> asyncLetterCall =
        processLetterService.processLetterService(collectionExercise, eventTag, instant);
    boolean emailStatus = asyncEmailCall.get();
    boolean letterStatus = asyncLetterCall.get();
    if (emailStatus && letterStatus) {
      newRequest.setStatus(ActionEventRequestStatus.COMPLETED);
      log.with("collectionExerciseId", collectionExerciseId)
          .with("eventTag", eventTag)
          .info("Requested event is now successfully completed.");
    } else {
      newRequest.setStatus(ActionEventRequestStatus.RETRY);
      log.with("collectionExerciseId", collectionExerciseId)
          .with("eventTag", eventTag)
          .with("emailAsyncStatus", emailStatus)
          .with("letterAsyncStatus", letterStatus)
          .info("Requested event was not successful, hence a retry will be initiated soon.");
    }
    actionEventRequestRepository.save(newRequest);
    log.with("collectionExerciseId", collectionExerciseId)
        .with("eventTag", eventTag)
        .info("Processing finished.");
    updateCollectionExerciseEventStatus(newRequest, event);
  }

  /**
   * sends action case event request current status to collection exercise
   *
   * @param request
   * @param event
   * @throws JsonProcessingException
   */
  private void updateCollectionExerciseEventStatus(
      CaseActionEventRequest request, CaseActionEvent event) throws JsonProcessingException {
    CaseActionEvent caseActionEvent = (null != event) ? event : new CaseActionEvent();
    caseActionEvent.setStatus(request.getStatus());
    if (event == null) {
      caseActionEvent.setCollectionExerciseID(request.getCollectionExerciseId());
      caseActionEvent.setTag(CaseActionEvent.EventTag.valueOf(request.getEventTag()));
    }
    log.with("event", event).info("updating collection exercise event status");
    collectionExerciseEventStatusUpdate.sendToPubSub(
        objectMapper.writeValueAsString(caseActionEvent));
  }

  @Async
  public void retryEvents()
      throws ExecutionException, InterruptedException, JsonProcessingException {
    log.info("Starting retry Event processing");
    Instant instant = Instant.now();
    List<CaseActionEventRequest> existingRequests =
        actionEventRequestRepository.findByStatus(ActionEventRequestStatus.RETRY);
    if (existingRequests.isEmpty()) {
      log.info("No events are pending retry. Will try again next time.");
      return;
    }
    for (CaseActionEventRequest existingRequest : existingRequests) {
      CollectionExerciseDTO collectionExercise =
          getCollectionExercise(existingRequest.getCollectionExerciseId());
      existingRequest.setStatus(ActionEventRequestStatus.INPROGRESS);
      log.with("collectionExerciseId", existingRequest.getCollectionExerciseId())
          .with("eventTag", existingRequest.getEventTag())
          .debug("Retry event is now in progress.");
      actionEventRequestRepository.save(existingRequest);
      CaseActionEvent actionEvent = new CaseActionEvent();
      actionEvent.setTag(CaseActionEvent.EventTag.valueOf(existingRequest.getEventTag()));
      actionEvent.setCollectionExerciseID(existingRequest.getCollectionExerciseId());

      log.with("collectionExerciseId", existingRequest.getCollectionExerciseId())
          .with("eventTag", existingRequest.getEventTag())
          .debug("Retry event will now trigger email processing asynchronously.");
      Future<Boolean> asyncEmailCall =
          processEmailService.processEmailService(
              collectionExercise, existingRequest.getEventTag(), instant);
      log.with("collectionExerciseId", existingRequest.getCollectionExerciseId())
          .with("eventTag", existingRequest.getEventTag())
          .debug("Retry event will now trigger letter processing asynchronously.");
      Future<Boolean> asyncLetterCall =
          processLetterService.processLetterService(
              collectionExercise, existingRequest.getEventTag(), instant);

      boolean emailStatus = asyncEmailCall.get();
      boolean letterStatus = asyncLetterCall.get();
      if (emailStatus && letterStatus) {
        existingRequest.setStatus(ActionEventRequestStatus.COMPLETED);
        log.with("collectionExerciseId", existingRequest.getCollectionExerciseId())
            .with("eventTag", existingRequest.getEventTag())
            .debug("Retry event is now successfully completed");
      } else {
        existingRequest.setStatus(ActionEventRequestStatus.FAILED);
        log.with("collectionExerciseId", existingRequest.getCollectionExerciseId())
            .with("eventTag", existingRequest.getEventTag())
            .debug("Retry event has failed.");
      }
      actionEventRequestRepository.save(existingRequest);
      updateCollectionExerciseEventStatus(existingRequest, null);
    }
    log.info("retry Event finished");
  }

  /**
   * Gets collection exercise dto against collection exercise id
   *
   * @param collectionExerciseId
   * @return
   */
  private CollectionExerciseDTO getCollectionExercise(UUID collectionExerciseId) {
    log.with("collectionExerciseId", collectionExerciseId).debug("Getting collectionExercise");
    return collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
  }
}
