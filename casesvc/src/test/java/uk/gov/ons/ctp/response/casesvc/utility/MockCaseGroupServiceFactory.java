package uk.gov.ons.ctp.response.casesvc.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;

import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 */
public final class MockCaseGroupServiceFactory implements Factory<CaseGroupService> {

  /**
   * provide method
   * @return mocked service
   */
  public CaseGroupService provide() {

    final CaseGroupService mockedService = Mockito.mock(CaseGroupService.class);

//    Mockito.when(mockedService.findCaseTypes()).thenAnswer(new Answer<List<CaseType>>() {
//      public List<CaseType> answer(final InvocationOnMock invocation)
//          throws Throwable {
//        List<CaseType> result = new ArrayList<CaseType>();
//        result.add(new CaseType(1, CASETYPE1_NAME, CASETYPE1_DESC, CASETYPE1_ACTIONPLANID, CASETYPE1_QUESTIONSET));
//        result.add(new CaseType(2, CASETYPE2_NAME, CASETYPE2_DESC, CASETYPE2_ACTIONPLANID, CASETYPE2_QUESTIONSET));
//        result.add(new CaseType(3, CASETYPE3_NAME, CASETYPE3_DESC, CASETYPE3_ACTIONPLANID, CASETYPE3_QUESTIONSET));
//        return result;
//      }
//    });
//
//    Mockito.when(mockedService.findCaseTypeByCaseTypeId(CASETYPEID)).thenAnswer(new Answer<CaseType>() {
//      public CaseType answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return new CaseType(3, CASETYPE3_NAME, CASETYPE3_DESC, CASETYPE3_ACTIONPLANID, CASETYPE3_QUESTIONSET);
//      }
//    });
//
//    Mockito.when(mockedService.findCaseTypeByCaseTypeId(UNCHECKED_EXCEPTION))
//        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));
//
//    Mockito.when(mockedService.findCaseTypeByCaseTypeId(NON_EXISTING_CASETYPEID)).thenAnswer(new Answer<CaseType>() {
//      public CaseType answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return null;
//      }
//    });

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final CaseGroupService t) {
  }
}
