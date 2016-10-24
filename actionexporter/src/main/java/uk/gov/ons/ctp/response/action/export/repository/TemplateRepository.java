package uk.gov.ons.ctp.response.action.export.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;

/**
 * Mongo database repository for TemplateDocument entities
 */
@Repository
public interface TemplateRepository extends MongoRepository<TemplateDocument, String> {
}
