package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
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
  @Path("/{categoryName}")
  public Response findCategory(@PathParam("categoryName") final String categoryType) throws CTPException {
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
    return Response.ok(mapperFacade.map(category, CategoryDTO.class)).build();
  }

  /**
   * the GET endpoint to retrieve all categories
   * 
   * @param role the role
   * @return the list of categories
   */
  @GET
  public Response findCategories(@QueryParam("role") final String role, @QueryParam("group") final String group) {
    log.info("Entering findCategories with role {}", role);
    List<Category> categories = categoryService.findCategories(role, group);
    List<CategoryDTO> categoryDTOs = mapperFacade.mapAsList(categories, CategoryDTO.class);
    return Response.ok(CollectionUtils.isEmpty(categoryDTOs) ? null : categoryDTOs).build();
  }
}
