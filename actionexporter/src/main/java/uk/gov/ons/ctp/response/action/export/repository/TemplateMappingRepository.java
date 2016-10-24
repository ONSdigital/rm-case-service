package uk.gov.ons.ctp.response.action.export.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;

/**
 * Mongo database repository for TemplateMappingDocument entities
 */
@Repository
public interface TemplateMappingRepository extends MongoRepository<TemplateMappingDocument, String> {
}

