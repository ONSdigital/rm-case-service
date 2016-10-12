package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/**
 * JPA Data Repository.
 */
@Repository
public interface CategoryRepository extends JpaRepository<Category, CategoryDTO.CategoryType> {


}
