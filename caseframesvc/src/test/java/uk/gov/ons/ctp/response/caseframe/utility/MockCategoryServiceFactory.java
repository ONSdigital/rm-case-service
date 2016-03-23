package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.Category;
import uk.gov.ons.ctp.response.caseframe.service.CategoryService;

/**
 * Created by Chris Parker 23.3.2016
 */
public final class MockCategoryServiceFactory implements Factory<CategoryService> {
  public static final String CATEGORY1_NAME = "ActionCompleted";
  public static final String CATEGORY2_NAME = "Address Details Incorrect";
  public static final String CATEGORY3_NAME = "Complaint - Escalated";
  public static final String CATEGORY1_DESC = "Action completed";
  public static final String CATEGORY2_DESC = "";
  public static final String CATEGORY3_DESC = "";
  public static final boolean CATEGORY1_MANUAL = false;
  public static final boolean CATEGORY2_MANUAL = false;
  public static final boolean CATEGORY3_MANUAL = true;
  public static final String NON_EXISTING_CATEGORYNAME = "UnknownName";
  public static final String UNCHECKED_EXCEPTION = "Not Found";
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * @return mocked service
   */
  public CategoryService provide() {

    final CategoryService mockedService = Mockito.mock(CategoryService.class);

    Mockito.when(mockedService.findCategories()).thenAnswer(new Answer<List<Category>>() {
      public List<Category> answer(final InvocationOnMock invocation)
          throws Throwable {
        List<Category> result = new ArrayList<Category>();
        result.add(new Category(CATEGORY1_NAME, CATEGORY1_DESC, CATEGORY1_MANUAL));
        result.add(new Category(CATEGORY2_NAME, CATEGORY2_DESC, CATEGORY2_MANUAL));
        result.add(new Category(CATEGORY3_NAME, CATEGORY3_DESC, CATEGORY3_MANUAL));
        return result;
      }
    });
    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final CategoryService t) {
  }
}
