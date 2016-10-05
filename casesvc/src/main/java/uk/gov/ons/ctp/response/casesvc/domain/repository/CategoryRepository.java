package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Category;

/**
 * JPA Data Repository.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {

  /**
   * To find a category by security role
   * @param role the security role
   * @return the found category
   */
  List<Category> findByRoleContaining(String role);
}
