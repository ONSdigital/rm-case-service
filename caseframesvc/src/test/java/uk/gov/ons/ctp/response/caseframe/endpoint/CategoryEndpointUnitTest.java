package uk.gov.ons.ctp.response.caseframe.endpoint;

import javax.ws.rs.core.Application;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.jersey.CTPJerseyTest;
import uk.gov.ons.ctp.response.caseframe.CaseFrameBeanMapper;
import uk.gov.ons.ctp.response.caseframe.service.CategoryService;
import uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory;

import static uk.gov.ons.ctp.response.caseframe.utility.MockCategoryServiceFactory.*;

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
        .assertStringListInBody("$..role", CATEGORY1_ROLE, CATEGORY2_ROLE, CATEGORY3_ROLE)
        .assertStringListInBody("$..generatedActionType",
            CATEGORY1_ACTIONTYPE, CATEGORY2_ACTIONTYPE, CATEGORY3_ACTIONTYPE)
        .assertBooleanListInBody("$..manual", CATEGORY1_MANUAL, CATEGORY2_MANUAL, CATEGORY3_MANUAL)
        .assertBooleanListInBody("$..closeCase", CATEGORY1_CLOSECASE, CATEGORY2_CLOSECASE, CATEGORY3_CLOSECASE)
        .andClose();
  }

}
