package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.Action;

/**
 * JPA Data Respository
 */
@Repository
public interface ActionRepository extends JpaRepository<Action, Integer> {

    List<Action> findByCaseId(Integer caseId);

}
