package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.ADMIN_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_GROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_LONG_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_MANUAL;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_SHORT_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_GROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_LONG_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_MANUAL;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_SHORT_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_GROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_LONG_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_MANUAL;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_SHORT_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockMvcControllerAdviceHelper.mockAdviceFor;

import ma.glasnost.orika.MapperFacade;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.HttpStatus;

import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * A test of the category endpoint
 *
 */
public final class CategoryEndpointUnitTest {

  @InjectMocks
  private CategoryEndpoint categoryEndpoint;

  @Mock
  private CategoryService categoryService;

  @Spy
  private MapperFacade mapperFacade = new CaseSvcBeanMapper();

  private MockMvc mockMvc;

  private static final String CATEGORY_UNKNOWN = "Felix The Cat";

  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc = MockMvcBuilders
            .standaloneSetup(categoryEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .build();
  }

  /**
   * A test
   */
  @Test
  public void findCategoriesNotFound() throws Exception {
    ResultActions actions = mockMvc.perform(getJson(String.format("/categories/%s", CATEGORY_UNKNOWN)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CategoryEndpoint.class));
    actions.andExpect(handler().methodName("findCategory"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(jsonPath("$.error.message", is(String.format("Category not found for categoryName %s", CATEGORY_UNKNOWN))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * A test
   */
  @Test
  public void findCategoriesFoundNoRoleSpecified() throws Exception {
    when(categoryService.findCategories(null, null)).thenReturn(FixtureHelper.loadClassFixtures(Category[].class));

    ResultActions actions = mockMvc.perform(getJson("/categories"));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CategoryEndpoint.class));
    actions.andExpect(handler().methodName("findCategories"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
    // TODO assert values for each param in 3

//        .assertStringListInBody("$..name", CATEGORY1_TYPE, CATEGORY2_TYPE, CATEGORY3_TYPE)
//        .assertStringListInBody("$..longDescription", CATEGORY1_LONG_DESC, CATEGORY2_LONG_DESC, CATEGORY3_LONG_DESC)
//        .assertStringListInBody("$..shortDescription", CATEGORY1_SHORT_DESC, CATEGORY2_SHORT_DESC, CATEGORY3_SHORT_DESC)
//        .assertStringListInBody("$..role", CATEGORY1_ROLE, CATEGORY2_ROLE, CATEGORY3_ROLE)
//        .assertStringListInBody("$..group", CATEGORY1_GROUP, CATEGORY2_GROUP, CATEGORY3_GROUP)
//        .assertBooleanListInBody("$..manual", CATEGORY1_MANUAL, CATEGORY2_MANUAL, CATEGORY3_MANUAL)
//        .andClose();
  }
//
//  /**
//   * A test
//   */
//  @Test
//  public void findCategoriesFoundAdminRoleSpecified() {
//    String serviceUrl = String.format("/categories?role=%s", ADMIN_ROLE);
//    with(serviceUrl)
//        .assertResponseCodeIs(HttpStatus.OK)
//        .assertArrayLengthInBodyIs(1)
//        .assertStringListInBody("$..name", CATEGORY1_TYPE)
//        .assertStringListInBody("$..longDescription", CATEGORY1_LONG_DESC)
//        .assertStringListInBody("$..shortDescription", CATEGORY1_SHORT_DESC)
//        .assertStringListInBody("$..role", ADMIN_ROLE)
//        .assertStringListInBody("$..group", CATEGORY1_GROUP)
//        .assertBooleanListInBody("$..manual", CATEGORY1_MANUAL)
//        .andClose();
//  }

}
