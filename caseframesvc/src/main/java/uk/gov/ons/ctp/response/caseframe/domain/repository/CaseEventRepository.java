package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.CaseEvent;

/**
 * JPA Data Repository.
 */
@Repository
public interface CaseEventRepository extends JpaRepository<CaseEvent, Integer> {

  /**
   * find the caseevent by id.
   * @param caseId to find by
   * @return the case event or null if not found
   */
  List<CaseEvent> findByCaseIdOrderByCreatedDateTimeDesc(Integer caseId);

}
