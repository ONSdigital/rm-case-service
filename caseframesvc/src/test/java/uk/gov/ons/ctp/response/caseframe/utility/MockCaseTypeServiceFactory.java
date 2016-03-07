package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.CaseType;
import uk.gov.ons.ctp.response.caseframe.service.CaseTypeService;

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
  public static final Integer CASETYPE1_ACTIONPLANID = 1;
  public static final Integer CASETYPE2_ACTIONPLANID = 2;
  public static final Integer CASETYPE3_ACTIONPLANID = 3;
  public static final String CASETYPE1_QUESTIONSET = "HH";
  public static final String CASETYPE2_QUESTIONSET = "CE";
  public static final String CASETYPE3_QUESTIONSET = "CE";
  public static final Integer CASETYPEID = 3;
  public static final Integer NON_EXISTING_CASETYPEID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * @return mocked service
   */
  public CaseTypeService provide() {

    final CaseTypeService mockedService = Mockito.mock(CaseTypeService.class);

    Mockito.when(mockedService.findCaseTypes()).thenAnswer(new Answer<List<CaseType>>() {
      public List<CaseType> answer(final InvocationOnMock invocation)
          throws Throwable {
        List<CaseType> result = new ArrayList<CaseType>();
        result.add(new CaseType(1, CASETYPE1_NAME, CASETYPE1_DESC, CASETYPE1_ACTIONPLANID, CASETYPE1_QUESTIONSET));
        result.add(new CaseType(2, CASETYPE2_NAME, CASETYPE2_DESC, CASETYPE2_ACTIONPLANID, CASETYPE2_QUESTIONSET));
        result.add(new CaseType(3, CASETYPE3_NAME, CASETYPE3_DESC, CASETYPE3_ACTIONPLANID, CASETYPE3_QUESTIONSET));
        return result;
      }
    });

    Mockito.when(mockedService.findCaseTypeByCaseTypeId(CASETYPEID)).thenAnswer(new Answer<CaseType>() {
      public CaseType answer(final InvocationOnMock invocation)
          throws Throwable {
        return new CaseType(3, CASETYPE3_NAME, CASETYPE3_DESC, CASETYPE3_ACTIONPLANID, CASETYPE3_QUESTIONSET);
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

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final CaseTypeService t) {
  }
}
