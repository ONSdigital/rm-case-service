package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/**
 * The Category Service interface defines all business behaviours for operations
 * on the Category entity model.
 */
public interface CategoryService extends CTPService {

  /**
   * Return all Categories, optionally filtered. NOTE: if a filter arg is passed
   * as null or "" it is implied that that filter should not be applied, ie that
   * filter should be wildcarded.
   *
   * @param role The optional security role to filter by
   * @param group The optional group to filter by
   * @return List of Category entities or empty List
   */
  List<Category> findCategories(String role, String group);

  /**
   * Find a category by its primary key name
   * 
   * @param categoryType the type/name
   * @return the Category or null if not found
   */
  Category findCategory(CategoryDTO.CategoryType categoryType);

}
