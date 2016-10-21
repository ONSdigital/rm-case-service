package uk.gov.ons.ctp.response.action.export.service.impl;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.ActionExportService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Service implementation responsible for persisting action export requests
 */
@Named
@Slf4j
public class ActionExportServiceImpl implements ActionExportService {

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
    log.debug("Saving {} actionRequests", instruction.getActionRequests().getActionRequests().size());
    List<ActionRequestDocument> actionRequests = mapperFacade.mapAsList(
        instruction.getActionRequests().getActionRequests(),
        ActionRequestDocument.class);
    Date now = new Date();
    actionRequests.forEach(actionRequest -> {
      actionRequest.setDateStored(now);
    });
    actionRequestRepo.save(actionRequests);
  }

  @Override
  public ActionRequestDocument findActionRequestDocument(final BigInteger actionId) {
    log.debug("Entering findActionPlan with {}", actionId);
    return actionRequestRepo.findOne(actionId);
  }

  @Override
  public ActionRequestDocument save(final ActionRequestDocument actionRequest) {
    log.debug("Saving ActionRequestDocument {}", actionRequest.getActionId());
    return actionRequestRepo.save(actionRequest);
  }
}
