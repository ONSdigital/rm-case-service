package uk.gov.ons.ctp.response.casesvc.service.action;

import static net.logstash.logback.argument.StructuredArguments.kv;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    log.info(
        "Started processing",
        kv("collectionExerciseId", collectionExerciseId),
        kv("eventTag", eventTag));
    Instant instant = Instant.now();
    CollectionExerciseDTO collectionExercise = getCollectionExercise(collectionExerciseId);
    // Check if ProcessActionEventRequest table for existing status else add it.
    List<CaseActionEventRequest> existingRequests =
        actionEventRequestRepository.findByCollectionExerciseIdAndEventTag(
            collectionExerciseId, eventTag);
    if (!existingRequests.isEmpty()) {
      log.warn(
          "Aborting processing event as an existing request is in progress.",
          kv("collectionExerciseId", collectionExerciseId),
          kv("eventTag", eventTag));
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
    log.info(
        "Requested event is now in progress.",
        kv("collectionExerciseId", collectionExerciseId),
        kv("eventTag", eventTag));
    log.debug(
        "Requested event will now trigger email processing asynchronously.",
        kv("collectionExerciseId", collectionExerciseId),
        kv("eventTag", eventTag));
    Future<Boolean> asyncEmailCall =
        processEmailService.processEmailService(collectionExercise, eventTag, instant);
    log.debug(
        "Requested event will now trigger letter processing asynchronously.",
        kv("collectionExerciseId", collectionExerciseId),
        kv("eventTag", eventTag));
    Future<Boolean> asyncLetterCall =
        processLetterService.processLetterService(collectionExercise, eventTag, instant);
    boolean emailStatus = asyncEmailCall.get();
    boolean letterStatus = asyncLetterCall.get();
    if (emailStatus && letterStatus) {
      newRequest.setStatus(ActionEventRequestStatus.COMPLETED);
      log.info(
          "Requested event is now successfully completed.",
          kv("collectionExerciseId", collectionExerciseId),
          kv("eventTag", eventTag));
    } else {
      newRequest.setStatus(ActionEventRequestStatus.RETRY);
      log.info(
          "Requested event was not successful, hence a retry will be initiated soon.",
          kv("collectionExerciseId", collectionExerciseId),
          kv("eventTag", eventTag),
          kv("emailAsyncStatus", emailStatus),
          kv("letterAsyncStatus", letterStatus));
    }
    actionEventRequestRepository.save(newRequest);
    log.info(
        "Processing finished.",
        kv("collectionExerciseId", collectionExerciseId),
        kv("eventTag", eventTag));
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
    ActionEventRequestStatus status =
        request.getStatus().equals(ActionEventRequestStatus.COMPLETED)
            ? ActionEventRequestStatus.PROCESSED
            : request.getStatus();
    caseActionEvent.setStatus(status);
    if (event == null) {
      caseActionEvent.setCollectionExerciseID(request.getCollectionExerciseId());
      caseActionEvent.setTag(CaseActionEvent.EventTag.valueOf(request.getEventTag()));
    }
    log.info("updating collection exercise event status", kv("event", event));
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
      log.debug(
          "Retry event is now in progress.",
          kv("collectionExerciseId", existingRequest.getCollectionExerciseId()),
          kv("eventTag", existingRequest.getEventTag()));
      actionEventRequestRepository.save(existingRequest);
      CaseActionEvent actionEvent = new CaseActionEvent();
      actionEvent.setTag(CaseActionEvent.EventTag.valueOf(existingRequest.getEventTag()));
      actionEvent.setCollectionExerciseID(existingRequest.getCollectionExerciseId());

      log.debug(
          "Retry event will now trigger email processing asynchronously.",
          kv("collectionExerciseId", existingRequest.getCollectionExerciseId()),
          kv("eventTag", existingRequest.getEventTag()));
      Future<Boolean> asyncEmailCall =
          processEmailService.processEmailService(
              collectionExercise, existingRequest.getEventTag(), instant);
      log.debug(
          "Retry event will now trigger letter processing asynchronously.",
          kv("collectionExerciseId", existingRequest.getCollectionExerciseId()),
          kv("eventTag", existingRequest.getEventTag()));
      Future<Boolean> asyncLetterCall =
          processLetterService.processLetterService(
              collectionExercise, existingRequest.getEventTag(), instant);

      boolean emailStatus = asyncEmailCall.get();
      boolean letterStatus = asyncLetterCall.get();
      if (emailStatus && letterStatus) {
        existingRequest.setStatus(ActionEventRequestStatus.COMPLETED);
        log.debug(
            "Retry event is now successfully completed",
            kv("collectionExerciseId", existingRequest.getCollectionExerciseId()),
            kv("eventTag", existingRequest.getEventTag()));
      } else {
        existingRequest.setStatus(ActionEventRequestStatus.FAILED);
        log.debug(
            "Retry event has failed.",
            kv("collectionExerciseId", existingRequest.getCollectionExerciseId()),
            kv("eventTag", existingRequest.getEventTag()));
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
    log.debug("Getting collectionExercise", kv("collectionExerciseId", collectionExerciseId));
    return collectionExerciseSvcClient.getCollectionExercise(collectionExerciseId);
  }
}
