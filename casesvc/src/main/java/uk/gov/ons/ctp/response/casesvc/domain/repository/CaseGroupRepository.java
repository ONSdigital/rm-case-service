package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;

/**
 * JPA Data Repository.
 */
@Repository
public interface CaseGroupRepository extends JpaRepository<CaseGroup, Integer> {

 /**
  * find all case groups associated with a uprn 
  * @param uprn the uprn
  * @return the list, empty or otherwise of groups associated with the uprn
  */
 List<CaseGroup> findByUprn(Long uprn);
}
