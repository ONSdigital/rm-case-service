package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.sample.representation.SampleUnitDTO;

/** JPA Data Repository. */
@Repository
@Transactional(readOnly = true)
public interface CaseRepository extends JpaRepository<Case, Integer> {

  /**
   * Return all cases in the given states and not in the list of excluded ids using the page
   * specification provided
   *
   * @param states States of Case
   * @param casePKs casepks to exclude
   * @param pageable the paging info for the query
   * @return List<Action> returns all cases in states, for the given page
   */
  List<Case> findByStateInAndCasePKNotIn(
      List<CaseState> states, List<Integer> casePKs, Pageable pageable);

  /**
   * Find cases assigned to the given casegroupid
   *
   * @param caseGroupFK the group id
   * @return the cases in the group
   */
  List<Case> findByCaseGroupFKOrderByCreatedDateTimeDesc(Integer caseGroupFK);

  /**
   * Find cases assigned to the given casegroupid
   *
   * @param caseGroupFK the case group UUID
   * @return the cases in the group
   */
  List<Case> findByCaseGroupId(UUID caseGroupFK);

  /**
   * Find cases assigned to the given casegroupid in a given state
   *
   * @param caseGroupFK the case group UUID
   * @param state the case group state
   * @return the cases in the group
   */
  List<Case> findByCaseGroupIdAndStateAndSampleUnitTypeOrderByCreatedDateTimeAsc(
      UUID caseGroupFK, CaseState state, SampleUnitDTO.SampleUnitType sampleUnitType);

  /**
   * Find cases assigned to the given iac There should only be one - it is the job of the caller to
   * complain if there is >1
   *
   * @param iac the iac
   * @return the cases associated with the IAC (see above)
   */
  List<Case> findByIacAudits_iac(String iac);

  /**
   * Find a case by its external case reference
   *
   * @param caseRef the external ref
   * @return the case
   */
  Case findByCaseRef(String caseRef);

  /**
   * Find a case by its UUID
   *
   * @param id the UUID
   * @return the case
   */
  Case findById(UUID id);

  /**
   * Find cases assigned to a given partyId
   *
   * @param partyId the partyId
   * @return the cases associated with the partyId
   */
  List<Case> findByPartyId(UUID partyId);

  /**
   * Find case by its sample unit id
   *
   * @param sampleUnitId
   * @return
   */
  Case findBySampleUnitId(@Param("sampleUnitId") UUID sampleUnitId);

  /**
   * Find a case by it's PK
   *
   * @param casePK
   * @return
   */
  Case findByCasePK(int casePK);
}
