package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Named;
import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import uk.gov.ons.ctp.response.caseframe.domain.model.Case;

/**
 * JPA Data Repository.
 */
@Named
@Transactional
public interface CaseRepository extends JpaRepository<Case, Integer> {

  /**
   * find the Cases by Statu and ActionPlanId
   * @param statu case statu to find by
   * @param actionPlanId actionPlan id to find by
   * @return the cases found
   */
  @Query(value = "SELECT caseId FROM caseframe.case WHERE state LIKE ?1 AND actionplanid = ?2", nativeQuery = true)
  List<BigInteger> findCaseIdsByStateAndActionPlanId(String state, Integer actionPlanId);

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
  @Query(value = "UPDATE caseframe.case SET state = ?1 WHERE caseid = ?2", nativeQuery = true)
  int setStatusFor(String status, Integer caseid);
}
