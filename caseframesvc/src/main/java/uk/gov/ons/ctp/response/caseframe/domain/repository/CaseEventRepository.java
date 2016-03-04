package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;

/**
 * JPA Data Repository
 */
@Repository
public interface CaseEventRepository extends JpaRepository<CaseEvent, Integer> {

  List<CaseEvent> findByCaseId(Integer caseId);

}
