package uk.gov.ons.ctp.response.casesvc.endpoint;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.Is.isA;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.handler;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static uk.gov.ons.ctp.common.MvcHelper.getJson;
import static uk.gov.ons.ctp.common.utility.MockMvcControllerAdviceHelper.mockAdviceFor;
import static uk.gov.ons.ctp.response.casesvc.endpoint.CategoryEndpoint.ERRORMSG_CATEGORYNOTFOUND;

import java.util.ArrayList;
import java.util.List;
import ma.glasnost.orika.MapperFacade;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.error.RestExceptionHandler;
import uk.gov.ons.ctp.common.jackson.CustomObjectMapper;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/** A test of the category endpoint */
public final class CategoryEndpointUnitTest {

  private static final String CATEGORY_UNKNOWN = "Felix The Cat";
  private static final String CATEGORY1_NAME = "ACCESSIBILITY_MATERIALS";
  private static final String CATEGORY2_NAME = "ACTION_CANCELLATION_COMPLETED";
  private static final String CATEGORY3_NAME = "ACTION_CANCELLATION_CREATED";
  private static final String CATEGORY1_SHORT_DESC = "Accessibility Materials";
  private static final String CATEGORY2_SHORT_DESC = "Action Cancellation Completed";
  private static final String CATEGORY3_SHORT_DESC = "Action Cancellation Created";
  private static final String CATEGORY1_LONG_DESC = "Accessibility Materials Blah";
  private static final String CATEGORY2_LONG_DESC = "Action Cancellation Completed Blah";
  private static final String CATEGORY3_LONG_DESC = "Action Cancellation Created Blah";
  private static final String CATEGORY1_ROLE = "collect-csos, collect-admins";
  private static final String ADMIN_ROLE = "collect-admins";
  private static final String CATEGORY1_GROUP = "general";
  @InjectMocks private CategoryEndpoint categoryEndpoint;
  @Mock private CategoryService categoryService;
  @Spy private MapperFacade mapperFacade = new CaseSvcBeanMapper();
  private MockMvc mockMvc;
  private List<Category> categoryResults;

  /**
   * Sets up Mockito for tests
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    MockitoAnnotations.initMocks(this);

    this.mockMvc =
        MockMvcBuilders.standaloneSetup(categoryEndpoint)
            .setHandlerExceptionResolvers(mockAdviceFor(RestExceptionHandler.class))
            .setMessageConverters(new MappingJackson2HttpMessageConverter(new CustomObjectMapper()))
            .build();

    this.categoryResults = FixtureHelper.loadClassFixtures(Category[].class);
  }

  /**
   * Get categories with no role or group specified. Results retrieved OK.
   *
   * @throws Exception if getJson fails
   */
  @Test
  public void findCategoriesFoundNoRoleSpecified() throws Exception {
    when(categoryService.findCategories(null, null)).thenReturn(categoryResults);

    ResultActions actions = mockMvc.perform(getJson("/categories"));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CategoryEndpoint.class));
    actions.andExpect(handler().methodName("findCategories"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(3)));
    actions.andExpect(jsonPath("$[0].*", Matchers.hasSize(5)));
    actions.andExpect(jsonPath("$[*].group", containsInAnyOrder(CATEGORY1_GROUP, null, null)));
    actions.andExpect(
        jsonPath("$[*].name", containsInAnyOrder(CATEGORY1_NAME, CATEGORY2_NAME, CATEGORY3_NAME)));
    actions.andExpect(
        jsonPath(
            "$[*].longDescription",
            containsInAnyOrder(CATEGORY1_LONG_DESC, CATEGORY2_LONG_DESC, CATEGORY3_LONG_DESC)));
    actions.andExpect(
        jsonPath(
            "$[*].shortDescription",
            containsInAnyOrder(CATEGORY1_SHORT_DESC, CATEGORY2_SHORT_DESC, CATEGORY3_SHORT_DESC)));
    actions.andExpect(jsonPath("$[*].role", containsInAnyOrder(CATEGORY1_ROLE, null, null)));
  }

  /**
   * Get categories with role ADMIN. Results retrieved OK.
   *
   * @throws Exception if getJson fails
   */
  @Test
  public void findCategoriesFoundAdminRoleSpecified() throws Exception {
    List<Category> results = new ArrayList<>();
    results.add(categoryResults.get(0));
    when(categoryService.findCategories(ADMIN_ROLE, null)).thenReturn(results);

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/categories?role=%s", ADMIN_ROLE)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CategoryEndpoint.class));
    actions.andExpect(handler().methodName("findCategories"));
    actions.andExpect(jsonPath("$", Matchers.hasSize(1)));
    actions.andExpect(jsonPath("$[0].*", Matchers.hasSize(5)));
    actions.andExpect(jsonPath("$[0].group", is(CATEGORY1_GROUP)));
    actions.andExpect(jsonPath("$[0].name", is(CATEGORY1_NAME)));
    actions.andExpect(jsonPath("$[0].longDescription", is(CATEGORY1_LONG_DESC)));
    actions.andExpect(jsonPath("$[0].shortDescription", is(CATEGORY1_SHORT_DESC)));
    actions.andExpect(jsonPath("$[0].role", is(CATEGORY1_ROLE)));
  }

  /**
   * Get category which does not exist.
   *
   * @throws Exception if getJson fails
   */
  @Test
  public void findACategoryNotFound() throws Exception {
    ResultActions actions =
        mockMvc.perform(getJson(String.format("/categories/name/%s", CATEGORY_UNKNOWN)));

    actions.andExpect(status().isNotFound());
    actions.andExpect(handler().handlerType(CategoryEndpoint.class));
    actions.andExpect(handler().methodName("findCategory"));
    actions.andExpect(jsonPath("$.error.code", is(CTPException.Fault.RESOURCE_NOT_FOUND.name())));
    actions.andExpect(
        jsonPath(
            "$.error.message",
            is(String.format("%s categoryName %s", ERRORMSG_CATEGORYNOTFOUND, CATEGORY_UNKNOWN))));
    actions.andExpect(jsonPath("$.error.timestamp", isA(String.class)));
  }

  /**
   * Get category which does exist.
   *
   * @throws Exception if getJson fails
   */
  @Test
  public void findACategoryFound() throws Exception {
    when(categoryService.findCategory(any())).thenReturn(categoryResults.get(0));

    ResultActions actions =
        mockMvc.perform(getJson(String.format("/categories/name/%s", CATEGORY1_NAME)));

    actions.andExpect(status().isOk());
    actions.andExpect(handler().handlerType(CategoryEndpoint.class));
    actions.andExpect(handler().methodName("findCategory"));
    actions.andExpect(jsonPath("$.*", Matchers.hasSize(5)));
    actions.andExpect(jsonPath("$.group", is(CATEGORY1_GROUP)));
    actions.andExpect(jsonPath("$.name", is(CATEGORY1_NAME)));
    actions.andExpect(jsonPath("$.longDescription", is(CATEGORY1_LONG_DESC)));
    actions.andExpect(jsonPath("$.shortDescription", is(CATEGORY1_SHORT_DESC)));
    actions.andExpect(jsonPath("$.role", is(CATEGORY1_ROLE)));
  }
}
