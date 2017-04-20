package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.Arrays;
import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CategoryService;

/**
 * Created by Chris Parker 23.3.2016
 */
public final class MockCategoryServiceFactory {
  public static final String CATEGORY1_TYPE = "GENERAL_ENQUIRY";
  public static final String CATEGORY2_TYPE = "ONLINE_QUESTIONNAIRE_RESPONSE";
  public static final String CATEGORY3_TYPE = "GENERAL_ENQUIRY_ESCALATED";
  public static final String CATEGORY1_SHORT_DESC = "General Enquiry";
  public static final String CATEGORY2_SHORT_DESC = "Online Questionnaire Response";
  public static final String CATEGORY3_SHORT_DESC = "General Enquiry Escalated";
  public static final String CATEGORY1_LONG_DESC = "General Enquiry Blah";
  public static final String CATEGORY2_LONG_DESC = "Online Questionnaire Response Blah";
  public static final String CATEGORY3_LONG_DESC = "General Enquiry Escalated Blah";
  public static final String CATEGORY1_ROLE = "collect-admins";
  public static final String CATEGORY2_ROLE = "collect-csos";
  public static final String CATEGORY3_ROLE = "collect-csos";
  public static final String ADMIN_ROLE = "collect-admins";
  public static final String CATEGORY1_GROUP = "general";
  public static final String CATEGORY2_GROUP = "general";
  public static final String CATEGORY3_GROUP = "general";
  public static final boolean CATEGORY1_MANUAL = true;
  public static final boolean CATEGORY2_MANUAL = false;
  public static final boolean CATEGORY3_MANUAL = true;
  public static final String UNCHECKED_EXCEPTION = "Not Found";
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * 
   * @return mocked service
   */
  public static CategoryService provide() {

    final CategoryService mockedService = Mockito.mock(CategoryService.class);

    try {
      List<Category> categories = FixtureHelper.loadClassFixtures(Category[].class);

      Mockito.when(mockedService.findCategories(null, null)).thenAnswer(new Answer<List<Category>>() {
        public List<Category> answer(final InvocationOnMock invocation)
            throws Throwable {
          return categories;
        }
      });
      Mockito.when(mockedService.findCategory(CategoryDTO.CategoryType.GENERAL_ENQUIRY)).thenAnswer(new Answer<Category>() {
        public Category answer(final InvocationOnMock invocation)
            throws Throwable {
          return categories.get(0);
        }
      });
      Mockito.when(mockedService.findCategories(ADMIN_ROLE, null)).thenAnswer(new Answer<List<Category>>() {
        public List<Category> answer(final InvocationOnMock invocation)
            throws Throwable {
          return Arrays.asList(categories.get(0));
        }
      });
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    return mockedService;
  }
}
