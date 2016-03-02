package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.ActionPlan;

/**
 * JPA Data Repository
 */
@Repository
public interface ActionPlanRepository extends JpaRepository<ActionPlan, Integer> {

}
