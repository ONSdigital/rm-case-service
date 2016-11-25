package uk.gov.ons.ctp.response.action.export.service;

import java.math.BigInteger;
import java.util.List;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;

/**
 * Service responsible for dealing with ActionRequests stored in MongoDB
 */
public interface ActionRequestService {
  /**
   * To retrieve all ActionRequestDocuments
   *
   * @return a list of ActionRequestDocuments
   */
  List<ActionRequestDocument> retrieveAllActionRequestDocuments();

  /**
   * To retrieve a given ActionRequestDocument
   *
   * @param actionId the ActionRequestDocument actionId to be retrieved
   * @return the given ActionRequestDocument
   */
  ActionRequestDocument retrieveActionRequestDocument(BigInteger actionId);

  /**
   * Save a ActionRequestDocument
   *
   * @param actionRequest the ActionRequestDocument to save.
   * @return the ActionRequestDocument saved.
   */
  ActionRequestDocument save(final ActionRequestDocument actionRequest);

  /**
   * Retrieve all action export requests not done for an actionType.
   * 
   * @return List ActionRequestDocuments not sent to external services
   *         previously for actionType.
   */
  List<ActionRequestDocument> findByDateSentIsNullAndActionType(String actionType);

  /**
   * Return a list of distinct actionTypes in collection
   *
   * @return a list of actionTypes.
   */
  List<String> retieveActionTypes();
}
