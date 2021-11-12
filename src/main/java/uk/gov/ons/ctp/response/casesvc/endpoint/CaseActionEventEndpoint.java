package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.concurrent.ExecutionException;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.casesvc.representation.action.CaseActionEvent;
import uk.gov.ons.ctp.response.casesvc.service.action.ProcessCaseActionEventService;

@RestController
@RequestMapping(produces = "application/json")
public class CaseActionEventEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CaseActionEventEndpoint.class);

  private final ProcessCaseActionEventService processEventService;

  @Autowired
  public CaseActionEventEndpoint(ProcessCaseActionEventService processEventService) {
    this.processEventService = processEventService;
  }

  @RequestMapping(
      value = "/process-event",
      method = RequestMethod.POST,
      consumes = "application/json")
  public ResponseEntity processEvents(@RequestBody @Valid CaseActionEvent event)
      throws ExecutionException, InterruptedException, JsonProcessingException {
    log.with("collectionExercise", event.getCollectionExerciseID())
        .with("EventTag", event.getTag())
        .info("Processing Event");
    processEventService.processEvents(event);
    return ResponseEntity.accepted().body(null);
  }

  @RequestMapping(value = "/retry-event", method = RequestMethod.POST)
  public ResponseEntity retryFailedEvents()
      throws ExecutionException, InterruptedException, JsonProcessingException {
    log.info("Initiating retry schedule for failed events if any.");
    processEventService.retryEvents();
    return ResponseEntity.accepted().body(null);
  }
}
