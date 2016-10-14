package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseType;
import uk.gov.ons.ctp.response.casesvc.service.CaseTypeService;

/**
 * Created by Martin.Humphrey on 26/2/2016.
 */
public final class MockCaseTypeServiceFactory implements Factory<CaseTypeService> {
  public static final String CASETYPE1_NAME = "HH";
  public static final String CASETYPE2_NAME = "CH";
  public static final String CASETYPE3_NAME = "HGH";
  public static final String CASETYPE1_DESC = "Household";
  public static final String CASETYPE2_DESC = "Care Home";
  public static final String CASETYPE3_DESC = "Hotel Guest House Bed and Breakfast";
  public static final String CASETYPE1_RESPONDENT = "H";
  public static final String CASETYPE2_RESPONDENT = "C";
  public static final String CASETYPE3_RESPONDENT = "G";
  public static final String CASETYPE1_QUESTIONSET = "QS1";
  public static final String CASETYPE2_QUESTIONSET = "QS2";
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
  public CaseTypeService provide() {

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

  /**
   * dispose method
   * 
   * @param t service to dispose
   */
  public void dispose(final CaseTypeService t) {
  }
}
