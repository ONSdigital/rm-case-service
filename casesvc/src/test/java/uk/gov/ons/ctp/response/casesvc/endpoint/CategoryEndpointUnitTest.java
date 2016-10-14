package uk.gov.ons.ctp.response.casesvc.endpoint;

import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.ADMIN_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_GROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_MANUAL;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY1_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_GROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_MANUAL;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY2_ROLE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_GROUP;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_DESC;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_MANUAL;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_TYPE;
import static uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory.CATEGORY3_ROLE;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.casesvc.CaseSvcBeanMapper;
import uk.gov.ons.ctp.response.casesvc.endpoint.CategoryEndpoint;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;
import uk.gov.ons.ctp.response.casesvc.utility.MockCategoryServiceFactory;

/**
 * A test of the category endpoint
 *
 */
public final class CategoryEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(CategoryEndpoint.class, CategoryService.class, MockCategoryServiceFactory.class,
        new CaseSvcBeanMapper());
  }

  /**
   * A test
   */
  @Test
  public void findCategoriesFoundNoRoleSpecified() {
    with("http://localhost:9998/categories")
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(3)
        .assertStringListInBody("$..name", CATEGORY1_TYPE, CATEGORY2_TYPE, CATEGORY3_TYPE)
        .assertStringListInBody("$..description", CATEGORY1_DESC, CATEGORY2_DESC, CATEGORY3_DESC)
        .assertStringListInBody("$..role", CATEGORY1_ROLE, CATEGORY2_ROLE, CATEGORY3_ROLE)
        .assertStringListInBody("$..group", CATEGORY1_GROUP, CATEGORY2_GROUP, CATEGORY3_GROUP)
        .assertBooleanListInBody("$..manual", CATEGORY1_MANUAL, CATEGORY2_MANUAL, CATEGORY3_MANUAL)
        .andClose();
  }

  /**
   * A test
   */
  @Test
  public void findCategoriesFoundAdminRoleSpecified() {
    String serviceUrl = String.format("http://localhost:9998/categories?role=%s", ADMIN_ROLE);
    with(serviceUrl)
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(1)
        .assertStringListInBody("$..name", CATEGORY1_TYPE)
        .assertStringListInBody("$..description", CATEGORY1_DESC)
        .assertStringListInBody("$..role", ADMIN_ROLE)
        .assertStringListInBody("$..group", CATEGORY1_GROUP)
        .assertBooleanListInBody("$..manual", CATEGORY1_MANUAL)
        .andClose();
  }

}
