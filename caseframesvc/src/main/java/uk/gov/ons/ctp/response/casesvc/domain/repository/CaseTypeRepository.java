package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;

/**
 * JPA Data Repository.
 */
@Repository
public interface CaseTypeRepository extends JpaRepository<CaseType, Integer> {

}
