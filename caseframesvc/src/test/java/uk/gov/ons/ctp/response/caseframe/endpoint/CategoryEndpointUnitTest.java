package uk.gov.ons.ctp.response.caseframe.endpoint;

import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY1_DESC;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY1_MANUAL;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY1_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY2_DESC;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY2_MANUAL;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY2_NAME;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY3_DESC;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY3_MANUAL;
import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.CATEGORY3_NAME;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.service.CategoryService;
import uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory;

/**
 * Created by Chris Parker 23.3.2016
 */
public final class CategoryEndpointUnitTest extends CTPJerseyTest {

  /**
   * configure the test
   */
  @Override
  public Application configure() {
    return super.init(CategoryEndpoint.class, CategoryService.class, MockCategoryServiceFactory.class,
        new CaseFrameBeanMapper());
  }

  /**
   * test findCategories
   */
  @Test
  public void findCategoriesFound() {
    with("http://localhost:9998/categories")
        .assertResponseCodeIs(HttpStatus.OK)
        .assertArrayLengthInBodyIs(3)
        .assertStringListInBody("$..name", CATEGORY1_NAME, CATEGORY2_NAME, CATEGORY3_NAME)
        .assertStringListInBody("$..description", CATEGORY1_DESC, CATEGORY2_DESC, CATEGORY3_DESC)     
        .assertBooleanListInBody("$..manual", CATEGORY1_MANUAL, CATEGORY2_MANUAL, CATEGORY3_MANUAL)
        .andClose();
  }

}
