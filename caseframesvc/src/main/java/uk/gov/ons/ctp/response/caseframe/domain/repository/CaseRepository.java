package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import uk.gov.ons.ctp.response.caseframe.domain.model.Case;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;

/**
 * JPA Data Repository.
 */
@Named
@Transactional
public interface CaseRepository extends JpaRepository<Case, Integer> {

  /**
   * find the Case by uprn.
   * @param uprn to find by
   * @return the case or null if not found
   */
  List<Case> findByUprn(Integer uprn);

  /**
   * set the case status for a given case.
   * @param status the case status value
   * @param caseid the case by id
   * @return the number of cases updated
   */
  @Modifying
  @Query(value = "UPDATE caseframe.case SET case_status = ?1 WHERE caseid = ?2", nativeQuery = true)
  int setStatusFor(String status, Integer caseid);
}
