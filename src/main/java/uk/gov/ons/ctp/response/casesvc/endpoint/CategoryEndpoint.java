package uk.gov.ons.ctp.response.casesvc.endpoint;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import java.util.List;
import java.util.Optional;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;

/** The REST endpoint controller for Category */
@RestController
@RequestMapping(value = "/categories", produces = "application/json")
public final class CategoryEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CategoryEndpoint.class);

  public static final String ERRORMSG_CATEGORYNOTFOUND = "Category not found for";

  @Autowired private CategoryService categoryService;

  @Qualifier("caseSvcBeanMapper")
  @Autowired
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve all categories
   *
   * @param role the role (example: collect-csos, collect-admins)
   * @param group the group (example: general)
   * @return the list of categories
   */
  @RequestMapping(method = RequestMethod.GET)
  public ResponseEntity<List<CategoryDTO>> findCategories(
      @RequestParam(value = "role", required = false) final String role,
      @RequestParam(value = "group", required = false) final String group) {
    log.with("role", role).debug("Entering findCategories");

    List<Category> categories = categoryService.findCategories(role, group);
    List<CategoryDTO> categoryDTOs = mapperFacade.mapAsList(categories, CategoryDTO.class);
    return CollectionUtils.isEmpty(categoryDTOs)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(categoryDTOs);
  }
}
