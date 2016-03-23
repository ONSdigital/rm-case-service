package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;

/**
 * The Category Service interface defines all business behaviours for operations
 * on the Category entity model.
 */
public interface CategoryService extends CTPService {

  /**
   * Return all Categories.
   *
   * @return List of Category entities or empty List
   */
  List<Category> findCategories();

}
