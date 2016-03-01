package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import uk.gov.ons.ctp.response.caseframe.domain.model.Case;

import javax.inject.Named;
import javax.transaction.Transactional;
import java.util.List;

/**
 * JPA Data Respository
 */
@Named
@Transactional
public interface CaseRepository extends JpaRepository<Case, Integer> {

    List<Case> findByUprn(Integer uprn);

    @Modifying
    @Query(value = "UPDATE caseframe.case SET case_status = ?1 WHERE caseid = ?2", nativeQuery = true)
    int setStatusFor(String status, Integer caseid);
}
