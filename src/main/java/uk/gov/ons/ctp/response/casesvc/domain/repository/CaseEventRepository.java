package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.Set;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;

/** JPA Data Repository. */
@Repository
public interface CaseEventRepository extends JpaRepository<CaseEvent, Integer> {

  /**
   * find the caseevent by FK.
   *
   * @param caseFK to find by
   * @return the case event or null if not found
   */
  List<CaseEvent> findByCaseFKOrderByCreatedDateTimeDesc(Integer caseFK);

  List<CaseEvent> findByCaseFKAndCategoryInOrderByCreatedDateTimeDesc(
      Integer caseFK, Set<CategoryName> categoryNames);
}
