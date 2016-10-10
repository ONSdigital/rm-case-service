package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/**
 * The REST endpoint controller for Category
 */
@Path("/categories")
@Produces({"application/json"})
@Slf4j
public final class CategoryEndpoint implements CTPEndpoint {
  public static final String ERRORMSG_CATEGORYNOTFOUND = "Category not found for";

  @Inject
  private CategoryService categoryService;

  @Inject
  private MapperFacade mapperFacade;

  /**
   * the GET endpoint to retrieve all categories
   * 
   * @param role the role
   * @return the list of categories
   */
  @GET
  @Path("/")
  public List<CategoryDTO> findCategories(@QueryParam("role") final String role) {
    log.debug("Entering findCategories with role {}", role);
    List<Category> categories = categoryService.findCategories(role);
    List<CategoryDTO> categoryDTOs = mapperFacade.mapAsList(categories, CategoryDTO.class);
    return CollectionUtils.isEmpty(categoryDTOs) ? null : categoryDTOs;
  }
}
