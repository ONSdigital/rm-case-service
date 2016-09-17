package uk.gov.ons.ctp.response.action.export.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;

import javax.inject.Named;

/**
 * Mongo database repository for FreeMarkerTemplate entities
 */
@Repository
@Named
public interface FreeMarkerTemplateRepository extends MongoRepository<FreeMarkerTemplate, String> {
}
