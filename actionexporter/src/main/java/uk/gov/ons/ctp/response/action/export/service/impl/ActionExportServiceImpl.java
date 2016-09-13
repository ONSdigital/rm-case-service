package uk.gov.ons.ctp.response.action.export.service.impl;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequest;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.ActionExportService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionInstruction;

/**
 * Service implementation responsible for persisting action export requests
 */
@Named
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
    List<ActionRequest> actionRequests = mapperFacade.mapAsList(instruction.getActionRequests().getActionRequests(),
        ActionRequest.class);
    Date now = new Date();
    actionRequests.forEach(actionRequest -> {
      actionRequest.setDateStored(now);
    });
    actionRequestRepo.save(actionRequests);
  }

}
