package uk.gov.ons.ctp.response.action.export.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;

import javax.inject.Inject;
import javax.inject.Named;
import java.math.BigInteger;
import java.util.List;

/**
 * The implementation of ActionRequestService
 */
@Named
@Slf4j
public class ActionRequestServiceImpl implements ActionRequestService {

  @Inject
  private ActionRequestRepository repository;

  @Override
  public List<ActionRequestDocument> retrieveAllActionRequestDocuments() {
    return repository.findAll();
  }

  @Override
  public ActionRequestDocument retrieveActionRequestDocument(BigInteger actionId) {
    return repository.findOne(actionId);
  }

  @Override
  public ActionRequestDocument save(final ActionRequestDocument actionRequest) {
    log.debug("Saving ActionRequestDocument {}", actionRequest.getActionId());
    return repository.save(actionRequest);
  }
}
