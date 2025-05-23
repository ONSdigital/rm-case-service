package uk.gov.ons.ctp.response.casesvc.service;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/**
 * A CategoryService implementation which encapsulates all business logic operating on the Category
 * entity model.
 */
@Service
public class CategoryService {
  private static final Logger log = LoggerFactory.getLogger(CategoryService.class);

  /** Spring Data Repository for CaseType entities. */
  @Autowired private CategoryRepository categoryRepo;

  /**
   * Find a category by its primary key name
   *
   * @param categoryName the type/name
   * @return the Category or null if not found
   */
  public Category findCategory(CategoryDTO.CategoryName categoryName) {
    log.debug("Entering findCategory", kv("category_name", categoryName));
    return categoryRepo.findById(categoryName).orElse(null);
  }

  /**
   * Finds categories by role and group
   *
   * @param role The optional security role to filter by
   * @param group The optional group to filter by
   * @return List<Category> List of categories
   */
  public List<Category> findCategories(String role, String group) {
    log.debug("Entering findCategories", kv("role", role), kv("group", group));
    List<Category> categories = categoryRepo.findAll();
    return filterCategories(categories, group, role);
  }

  /**
   * @param categories List<Category> of lists to filter
   * @param group how categories should be grouped
   * @param role what role categories should contain
   * @return List<Category> of filtered categories
   */
  private List<Category> filterCategories(List<Category> categories, String group, String role) {
    boolean roleFiltered = !StringUtils.isEmpty(role);
    boolean groupFiltered = !StringUtils.isEmpty(group);
    List<Category> filteredCategories =
        categories
            .stream()
            .filter(
                cat ->
                    (roleFiltered
                        ? (cat.getRole() == null ? false : cat.getRole().contains(role))
                        : true))
            .filter(
                cat ->
                    (groupFiltered
                        ? (cat.getGroup() == null ? false : cat.getGroup().contains(group))
                        : true))
            .sorted(
                (cat1, cat2) ->
                    cat1.getCategoryName().name().compareTo(cat2.getCategoryName().name()))
            .collect(Collectors.toList());
    return filteredCategories;
  }
}
