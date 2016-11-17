package uk.gov.ons.ctp.response.action.export.service.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.message.ActionFeedbackPublisher;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.ActionExportService;
import uk.gov.ons.ctp.response.action.message.feedback.ActionFeedback;
import uk.gov.ons.ctp.response.action.message.feedback.Outcome;
import uk.gov.ons.ctp.response.action.message.instruction.ActionCancel;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;
import uk.gov.ons.ctp.response.action.message.instruction.ActionRequest;

/**
 * Service implementation responsible for persisting action export requests
 */
@Named
@Slf4j
public class ActionExportServiceImpl implements ActionExportService {

  private static final String DATE_FORMAT = "dd/MM/yyyy HH:mm";

  @Inject
  private ActionFeedbackPublisher actionFeedbackPubl;

  @Inject
  private MapperFacade mapperFacade;

  @Inject
  private ActionRequestRepository actionRequestRepo;

  /**
   * Mongo database does not support transactions. Where transaction integrity
   * is important if failure occurs rollback must be manual or a single atomic
   * operation on the database undertaken which will succeed or fail e.g.
   * insertion of single document. Failure of operation will still result in
   * failure of any calling SI transaction and message being considered poisoned
   * bill and rejected to DLQ.
   *
   */
  @Override
  public void acceptInstruction(ActionInstruction instruction) {
    if (instruction.getActionRequests().getActionRequests().size() > 0) {
      processActionRequests(instruction.getActionRequests().getActionRequests());
    } else {
      log.info("No ActionRequests to process");
    }
    if (instruction.getActionCancels().getActionCancels().size() > 0) {
      processActionCancels(instruction.getActionCancels().getActionCancels());
    } else {
      log.info("No ActionCancels to process");
    }
  }

  /**
   * To process a list of actionRequests
   * @param actionRequests list to be processed
   */
  private void processActionRequests(List<ActionRequest> actionRequests) {
    log.debug("Saving {} actionRequests", actionRequests.size());
    List<ActionRequestDocument> actionRequestDocs = mapperFacade.mapAsList(actionRequests, ActionRequestDocument.class);
    Date now = new Date();
    actionRequestDocs.forEach(actionRequestDoc -> {
      actionRequestDoc.setDateStored(now);
    });
    actionRequestRepo.save(actionRequestDocs);
    String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(now);
    actionRequestDocs.forEach(actionRequestDoc -> {
      if (actionRequestDoc.isResponseRequired()) {
        ActionFeedback actionFeedback = new ActionFeedback(actionRequestDoc.getActionId(),
            "ActionExport Stored: " + timeStamp, Outcome.REQUEST_ACCEPTED, null);
        actionFeedbackPubl.sendActionFeedback(actionFeedback);
      }
    });
  }

  /**
   * To process a list of actionCancels
   * @param actionCancels list to be processed
   */
  private void processActionCancels(List<ActionCancel> actionCancels) {
    log.debug("Processing {} actionCancels", actionCancels.size());
    String timeStamp = new SimpleDateFormat(DATE_FORMAT).format(new Date());
    boolean cancelled = false;
    for (ActionCancel actionCancel : actionCancels) {
      ActionRequestDocument actionRequest = actionRequestRepo.findOne(actionCancel.getActionId());
      if (actionRequest != null && actionRequest.getDateSent() == null) {
        actionRequestRepo.delete(actionCancel.getActionId());
        cancelled = true;
      } else {
        cancelled = false;
      }
      if (actionCancel.isResponseRequired()) {
        ActionFeedback actionFeedback = new ActionFeedback(actionCancel.getActionId(),
            "ActionExport Cancelled: " + timeStamp,
            cancelled ? Outcome.CANCELLATION_COMPLETED : Outcome.CANCELLATION_FAILED, null);
        actionFeedbackPubl.sendActionFeedback(actionFeedback);
      }
    }
  }
}
