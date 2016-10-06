package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/**
 * A CategoryService implementation which encapsulates all business logic
 * operating on the Category entity model.
 */
@Named
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  /**
   * Spring Data Repository for CaseType entities.
   **/
  @Inject
  private CategoryRepository categoryRepo;


  @Override
  public Category findCategory(Integer categoryId) {
    log.debug("Entering findCategory with categoryId {}", categoryId);
    return categoryRepo.findOne(categoryId);
  }

  @Override
  public List<Category> findCategories(String role) {
    log.debug("Entering findCategories with role {}", role);
    if (role == null || role.isEmpty()) {
      return categoryRepo.findAll();
    } else {
      return categoryRepo.findByRoleContaining(role);
    }
  }

}
