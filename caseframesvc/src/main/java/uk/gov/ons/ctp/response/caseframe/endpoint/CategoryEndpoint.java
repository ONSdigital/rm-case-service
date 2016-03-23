package uk.gov.ons.ctp.response.caseframe.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.representation.CategoryDTO;
import uk.gov.ons.ctp.response.caseframe.service.CategoryService;

/**
 * The REST endpoint controller for Category
 */
@Path("/categories")
@Produces({ "application/json" })
@Slf4j
public final class CategoryEndpoint implements CTPEndpoint {

  @Inject
  private CategoryService categoryService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve all categories
   * @return the list of categories
   */
  @GET
  @Path("/")
  public List<CategoryDTO> findCategories() {
    log.debug("Entering findCategories...");
    List<Category> categories = categoryService.findCategories();
    List<CategoryDTO> categoryDTOs = mapperFacade.mapAsList(categories, CategoryDTO.class);
    return CollectionUtils.isEmpty(categoryDTOs) ? null : categoryDTOs;
  }

}
