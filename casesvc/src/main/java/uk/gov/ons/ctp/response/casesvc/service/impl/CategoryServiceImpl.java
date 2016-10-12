package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
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
  public Category findCategory(CategoryDTO.CategoryType categoryType) {
    log.debug("Entering findCategory with type {}", categoryType);
    return categoryRepo.findOne(categoryType);
  }
  
  @Override
  public List<Category> findCategories(String role, String group) {
    log.debug("Entering findCategories with role {} and group", role, group);
    List<Category> categories = categoryRepo.findAll();
    return categories.stream()
      .filter(cat->StringUtils.isEmpty(role)?true:cat.getRole().contains(role) && StringUtils.isEmpty(group)?true:cat.getGroup().contains(group))
      .collect(Collectors.toList());
  }

}
