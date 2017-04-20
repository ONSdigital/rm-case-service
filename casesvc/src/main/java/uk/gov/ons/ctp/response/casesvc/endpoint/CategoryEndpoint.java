package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.web.bind.annotation.*;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/**
 * The REST endpoint controller for Category
 */
@RestController
@RequestMapping(value = "/categories", produces = "application/json")
@Slf4j
public final class CategoryEndpoint implements CTPEndpoint {
  public static final String ERRORMSG_CATEGORYNOTFOUND = "Category not found for";

  @Autowired
  private CategoryService categoryService;

  @Autowired
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve the category with categoryType
   * 
   * @param categoryType the categoryType
   * @return the list of categories
   */
  @RequestMapping(value = "/{categoryName}", method = RequestMethod.GET)
  public CategoryDTO findCategory(@PathVariable("categoryName") final String categoryType) throws CTPException {
    log.info("Entering findCategory with categoryName {}", categoryType);

    Category category = null;
    Optional<CategoryDTO.CategoryType> catTypeEnum = CategoryDTO.CategoryType.fromString(categoryType);
    if (catTypeEnum.isPresent()) {
      category = categoryService.findCategory(catTypeEnum.get());
    }
    if (category == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
              String.format("%s categoryName %s", ERRORMSG_CATEGORYNOTFOUND, categoryType));
    }
    return mapperFacade.map(category, CategoryDTO.class);
  }

  /**
   * the GET endpoint to retrieve all categories
   * 
   * @param role the role
   * @return the list of categories
   */
  @RequestMapping(method = RequestMethod.GET)
  public List<CategoryDTO> findCategories(@RequestParam("role") final String role,
                                          @RequestParam("group") final String group) {
    log.info("Entering findCategories with role {}", role);
    List<Category> categories = categoryService.findCategories(role, group);
    List<CategoryDTO> categoryDTOs = mapperFacade.mapAsList(categories, CategoryDTO.class);
    return CollectionUtils.isEmpty(categoryDTOs) ? null : categoryDTOs;
  }
}
