package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

public final class MockCaseTypeServiceFactory {
  public static final String CASETYPE3_NAME = "HGH";
  public static final String CASETYPE3_DESC = "Hotel Guest House Bed and Breakfast";
  public static final String CASETYPE3_QUESTIONSET = "QS3";
  public static final Integer CASETYPEID = 3;
  public static final Integer NON_EXISTING_CASETYPEID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * 
   * @return mocked service
   */
  public static CaseTypeService provide() {

    final CaseTypeService mockedService = Mockito.mock(CaseTypeService.class);

    try {
      List<CaseType> cases = FixtureHelper.loadClassFixtures(CaseType[].class);

      Mockito.when(mockedService.findCaseTypes()).thenAnswer(new Answer<List<CaseType>>() {
        public List<CaseType> answer(final InvocationOnMock invocation)
            throws Throwable {
          return cases;
        }
      });

      Mockito.when(mockedService.findCaseTypeByCaseTypeId(CASETYPEID)).thenAnswer(new Answer<CaseType>() {
        public CaseType answer(final InvocationOnMock invocation)
            throws Throwable {
          return cases.get(2);
        }
      });

      Mockito.when(mockedService.findCaseTypeByCaseTypeId(UNCHECKED_EXCEPTION))
          .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

      Mockito.when(mockedService.findCaseTypeByCaseTypeId(NON_EXISTING_CASETYPEID)).thenAnswer(new Answer<CaseType>() {
        public CaseType answer(final InvocationOnMock invocation)
            throws Throwable {
          return null;
        }
      });
    } catch (Throwable t) {
      throw new RuntimeException(t);
    }

    return mockedService;
  }
}
