package uk.gov.ons.ctp.response.casesvc.endpoint;

import static net.logstash.logback.argument.StructuredArguments.kv;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.ObjectConverter;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.lib.common.endpoint.CTPEndpoint;

/** The REST endpoint controller for Category */
@RestController
@RequestMapping(value = "/categories", produces = "application/json")
public final class CategoryEndpoint implements CTPEndpoint {
  private static final Logger log = LoggerFactory.getLogger(CategoryEndpoint.class);

  public static final String ERRORMSG_CATEGORYNOTFOUND = "Category not found for";

  @Autowired private CategoryService categoryService;

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
    log.debug("Entering findCategories", kv("role", role));

    List<Category> categories = categoryService.findCategories(role, group);
    List<CategoryDTO> categoryDTOs = ObjectConverter.categoryDTO(categories);
    return CollectionUtils.isEmpty(categoryDTOs)
        ? ResponseEntity.noContent().build()
        : ResponseEntity.ok(categoryDTOs);
  }
}
