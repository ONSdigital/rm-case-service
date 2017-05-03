package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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
@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

  /**
   * Spring Data Repository for CaseType entities.
   **/
  @Autowired
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
    return filterCategories(categories, group, role);
  }
  
  private List<Category> filterCategories(List<Category> categories, String group, String role) {
    boolean roleFiltered = !StringUtils.isEmpty(role);
    boolean groupFiltered = !StringUtils.isEmpty(group);
    List<Category> filteredCategories = categories.stream()
        .filter(cat -> (roleFiltered ? (cat.getRole() == null ? false : cat.getRole().contains(role)) : true))
        .filter(cat -> (groupFiltered ? (cat.getGroup() == null ? false : cat.getGroup().contains(group)) : true))
        .sorted((cat1, cat2) -> cat1.getCategoryType().name().compareTo(cat2.getCategoryType().name()))
        .collect(Collectors.toList());
    return filteredCategories;
  }
}
