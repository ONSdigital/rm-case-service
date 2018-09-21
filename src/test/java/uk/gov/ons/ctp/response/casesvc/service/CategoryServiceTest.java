package uk.gov.ons.ctp.response.casesvc.service;

import static org.junit.Assert.assertNotNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CategoryRepository;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO.CategoryName;

@RunWith(MockitoJUnitRunner.class)
public class CategoryServiceTest {

  @Mock private CategoryRepository categoryRepo;

  @InjectMocks private CategoryService categoryService;

  private List<Category> categories;

  /**
   * All of these tests require the mocked repos to respond with predictable data loaded from test
   * fixture json files.
   *
   * @throws Exception exception thrown
   */
  @Before
  public void setUp() throws Exception {
    categories = FixtureHelper.loadClassFixtures(Category[].class);
  }

  @Test
  public void testFindCategorySuccess() {
    // Given
    given(categoryRepo.findOne(CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT))
        .willReturn(categories.get(0));

    // When
    categoryService.findCategory(CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);

    // Then
    verify(categoryRepo).findOne(CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT);
  }

  @Test
  public void testFindAllCategoriesSuccess() {
    // Given
    given(categoryRepo.findAll()).willReturn(categories);

    // When
    List<Category> response = categoryService.findCategories(null, null);

    // Then
    assertNotNull(response);
  }
}
