package uk.gov.ons.ctp.response.action.export.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;

/**
 * Mongo database repository for ActionRequest entities which is backed by
 * ActionRequest collection.
 */
@Repository
public interface ActionRequestRepository extends MongoRepository<ActionRequestDocument, Integer> {

}
