package uk.gov.ons.ctp.response.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.stream.Collectors;
import net.sourceforge.cobertura.CoverageIgnore;
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
@CoverageIgnore
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
    log.with("category_name", categoryName).debug("Entering findCategory");
    return categoryRepo.findOne(categoryName);
  }

  /**
   * Finds categories by role and group
   *
   * @param role The optional security role to filter by
   * @param group The optional group to filter by
   * @return List<Category> List of categories
   */
  public List<Category> findCategories(String role, String group) {
    log.with("role", role).with("group", group).debug("Entering findCategories");
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
