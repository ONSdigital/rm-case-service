package uk.gov.ons.ctp.response.casesvc.utility;

import java.sql.Timestamp;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;

import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * Mock CaseService response HK2 JSE JSR-330 dependency injection factory
 */
public final class MockCaseServiceFactory implements Factory<CaseService> {

  public static final Long UPRN = 2000062760L;
  public static final CaseDTO.CaseState CASE_STATE = CaseDTO.CaseState.SAMPLED_INIT;
  public static final Integer CASE1_TYPEID = 1;
  public static final Integer CASE2_TYPEID = 2;
  public static final Integer CASE3_TYPEID = 3;
  public static final String CREATEDDATE_VALUE = "2016-02-26T18:30:00.000+0000";
  public static final Timestamp CREATEDDATE_TIMESTAMP = Timestamp.valueOf("2016-02-26 18:30:00");
  public static final String CREATEDBY = "Unit Tester";
  public static final Integer CASE1_SAMPLEID = 1;
  public static final Integer CASE2_SAMPLEID = 2;
  public static final Integer CASE3_SAMPLEID = 3;
  public static final Integer CASE1_ACTIONPLANID = 1;
  public static final Integer CASE2_ACTIONPLANID = 2;
  public static final Integer CASE3_ACTIONPLANID = 3;
  public static final Integer CASE_SURVEYID = 1;
  public static final String CASE_QUESTIONSET = "HH";
  public static final Long NON_EXISTING_UPRN = 998L;
  public static final Integer EXISTING_ID = 991;
  public static final Integer NON_EXISTING_ID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final Integer CASEID = 124;
  public static final Integer QUESTIONNAIREID = 1234567907;
  public static final String CASEEVENT_DESC1 = "Test Event 1";
  public static final String CASEEVENT_DESC2 = "Test Event 2";
  public static final String CASEEVENT_DESC3 = "Test Event 3";
  public static final String CASEEVENT_CATEGORY = "General Enquiry";
  public static final String CASEEVENT_SUBCATEGORY = "Action Type Name";

  /**
   * provide method
   *
   * @return mocked service
   */
  public CaseService provide() {

    final CaseService mockedService = Mockito.mock(CaseService.class);
//
//    Mockito.when(mockedService.findCasesByUprn(UPRN)).thenAnswer(new Answer<List<Case>>() {
//      public List<Case> answer(final InvocationOnMock invocation)
//          throws Throwable {
//        List<Case> result = new ArrayList<Case>();
//        result.add(new Case(1, UPRN, CASE_STATE, CASE1_TYPEID, CREATEDDATE_TIMESTAMP, CREATEDBY,
//            CASE1_SAMPLEID, CASE1_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET));
//        result.add(new Case(2, UPRN, CASE_STATE, CASE2_TYPEID, CREATEDDATE_TIMESTAMP, CREATEDBY,
//            CASE2_SAMPLEID, CASE2_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET));
//        result.add(new Case(3, UPRN, CASE_STATE, CASE3_TYPEID, CREATEDDATE_TIMESTAMP, CREATEDBY,
//            CASE3_SAMPLEID, CASE3_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET));
//        return result;
//      }
//    });
//
//    Mockito.when(mockedService.findCasesByUprn(NON_EXISTING_UPRN)).thenAnswer(new Answer<List<Case>>() {
//      public List<Case> answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return new ArrayList<Case>();
//      }
//    });
//
//    Mockito.when(mockedService.findCaseByQuestionnaireId(QUESTIONNAIREID)).thenAnswer(new Answer<Case>() {
//      public Case answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return new Case(CASEID, UPRN, CASE_STATE, CASE1_TYPEID, CREATEDDATE_TIMESTAMP, CREATEDBY,
//            CASE1_SAMPLEID, CASE1_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET);
//      }
//    });
//
//    Mockito.when(mockedService.findCaseByQuestionnaireId(NON_EXISTING_ID)).thenAnswer(new Answer<Case>() {
//      public Case answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return null;
//      }
//    });
//
//    Mockito.when(mockedService.findCaseByCaseId(CASEID)).thenAnswer(new Answer<Case>() {
//      public Case answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return new Case(CASEID, UPRN, CASE_STATE, CASE1_TYPEID, CREATEDDATE_TIMESTAMP, CREATEDBY,
//            CASE1_SAMPLEID, CASE1_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET);
//      }
//    });
//
//    Mockito.when(mockedService.findCaseByCaseId(NON_EXISTING_ID)).thenAnswer(new Answer<Case>() {
//      public Case answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return null;
//      }
//    });
//
//    Mockito.when(mockedService.findCaseEventsByCaseId(CASEID)).thenAnswer(new Answer<List<CaseEvent>>() {
//      public List<CaseEvent> answer(final InvocationOnMock invocation)
//          throws Throwable {
//        List<CaseEvent> result = new ArrayList<CaseEvent>();
//        result.add(new CaseEvent(1, CASEID, CASEEVENT_DESC1, CREATEDBY, CREATEDDATE_TIMESTAMP, CASEEVENT_CATEGORY,
//            CASEEVENT_SUBCATEGORY));
//        result.add(new CaseEvent(2, CASEID, CASEEVENT_DESC2, CREATEDBY, CREATEDDATE_TIMESTAMP, CASEEVENT_CATEGORY,
//            CASEEVENT_SUBCATEGORY));
//        result.add(new CaseEvent(3, CASEID, CASEEVENT_DESC3, CREATEDBY, CREATEDDATE_TIMESTAMP, CASEEVENT_CATEGORY,
//            CASEEVENT_SUBCATEGORY));
//        return result;
//      }
//    });
//
//    Mockito.when(mockedService.findCaseEventsByCaseId(NON_EXISTING_ID)).thenAnswer(new Answer<List<CaseEvent>>() {
//      public List<CaseEvent> answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return new ArrayList<CaseEvent>();
//      }
//    });
//
//    Mockito.when(mockedService.findCaseByCaseId(UNCHECKED_EXCEPTION))
//        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));
//
//    Mockito.when(mockedService.createCaseEvent(any(CaseEvent.class))).thenAnswer(new Answer<CaseEvent>() {
//      public CaseEvent answer(final InvocationOnMock invocation)
//          throws Throwable {
//        return new CaseEvent(1, CASEID, CASEEVENT_DESC1, CREATEDBY, CREATEDDATE_TIMESTAMP, CASEEVENT_CATEGORY,
//            CASEEVENT_SUBCATEGORY);
//      }
//    });
//
//    Mockito.when(mockedService.findCaseIdsByStatesAndActionPlanId(anyObject(), eq(NON_EXISTING_ID))).thenAnswer(new Answer<List<Integer>>() {
//      public List<Integer> answer(final InvocationOnMock invocation)
//              throws Throwable {
//        return new ArrayList<Integer>();
//      }
//    });
//
//    Mockito.when(mockedService.findCaseIdsByStatesAndActionPlanId(anyObject(), eq(EXISTING_ID))).thenAnswer(new Answer<List<Integer>>() {
//      public List<Integer> answer(final InvocationOnMock invocation)
//              throws Throwable {
//        List<Integer> result = new ArrayList<>();
//        result.add(new Integer("1"));
//        return result;
//      }
//    });

    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final CaseService t) {
  }
}
