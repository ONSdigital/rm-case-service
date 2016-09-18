package uk.gov.ons.ctp.response.action.export.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;

/**
 * Mongo database repository for ContentDocument entities
 */
@Repository
public interface ContentRepository extends MongoRepository<ContentDocument, String> {
}
