package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import javax.inject.Named;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;

/**
 * JPA Data Repository.
 */
@Named
@Transactional(readOnly = true)
public interface CaseRepository extends JpaRepository<Case, Integer> {

  /**
   * Return all cases in the given states and not in the list of excluded ids
   * using the page specification provided
   *
   * @param states States of Case
   * @param caseIds caseIds to exclude
   * @param pageable the paging info for the query
   * @return List<Action> returns all cases in states, for the given page
   */
  List<Case> findByStateInAndCaseIdNotIn(List<CaseDTO.CaseState> states, List<Integer> caseIds, Pageable pageable);

  /**
   * set the case state for a given case.
   * 
   * @param state the case state value
   * @param caseid the case by id
   * @return the number of cases updated
   */
  @Modifying
  @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
  @Query(value = "UPDATE casesvc.case SET state = ?2 WHERE caseid = ?1", nativeQuery = true)
  int setState(Integer caseid, String state);
  
  
  /**
   * Find cases assigned to the given casegroupid
   * @param caseGroupId the group id
   * @return the cases in the group
   */
  List<Case> findByCaseGroupId(Integer caseGroupId);
  
  /**
   * Find cases assigned to the given iac
   * There should only be one - it is the job of the caller to complain if there is >1
   * @param caseGroupId the group id
   * @return the cases associated with the IAC (see above)
   */
  List<Case> findByIac(String iac);

  /**
   * Find a case by its external case reference
   * @param caseRef the external ref
   * @return the case
   */
  Case findByCaseRef(String caseRef);
  
}
