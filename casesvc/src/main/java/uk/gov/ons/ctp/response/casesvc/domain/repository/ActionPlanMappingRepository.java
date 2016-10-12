package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;

/**
 * JPA Data Repository.
 */
@Repository
public interface ActionPlanMappingRepository extends JpaRepository<ActionPlanMapping, Integer> {

 /**
  * find all ActionPlanMappings associated with a single casetype
  * @param caseTypeId identifies the casetype the mappings should be associated with
  * @return the list, empty or otherwise of case types
  */
 List<ActionPlanMapping> findByCaseTypeId(Integer caseTypeId);
}
