package uk.gov.ons.ctp.response.caseframe.utility;

import static uk.gov.ons.ctp.response.caseframe.utility.MockSurveyServiceFactory.NON_EXISTING_SURVEYID;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.springframework.http.HttpStatus;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.caseframe.domain.model.Action;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;
import uk.gov.ons.ctp.response.caseframe.service.CaseService;

/**
 * Created by Martin.Humphrey on 29/2/2016.
 */
public class MockCaseServiceFactory implements Factory<CaseService> {

  public static final Integer UPRN = 2000062760;
  public static final String CASE_STATUS = "INIT";
  public static final Integer CASE1_TYPEID = 1;
  public static final Integer CASE2_TYPEID = 2;
  public static final Integer CASE3_TYPEID = 3;
  public static final String CASE_CREATEDDATE_VALUE = "2016-02-26T18:30:00.000+0000";
  public static final Timestamp CASE_CREATEDDATE_TIMESTAMP = Timestamp.valueOf("2016-02-26 18:30:00");
  public static final String CASE_CREATEDBY = "Unit Tester";
  public static final Integer CASE1_SAMPLEID = 1;
  public static final Integer CASE2_SAMPLEID = 2;
  public static final Integer CASE3_SAMPLEID = 3;
  public static final Integer CASE1_ACTIONPLANID = 1;
  public static final Integer CASE2_ACTIONPLANID = 2;
  public static final Integer CASE3_ACTIONPLANID = 3;
  public static final Integer CASE_SURVEYID = 1;
  public static final Integer CASE_PARENTCASEID = null;
  public static final String CASE_QUESTIONSET = "HH";
  public static final Integer NON_EXISTING_ID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final Integer CASEID = 124;

  public CaseService provide() {

    final CaseService mockedService = Mockito.mock(CaseService.class);

    Mockito.when(mockedService.findCasesByUprn(UPRN)).thenAnswer(new Answer<List<Case>>() {
      public List<Case> answer(InvocationOnMock invocation)
          throws Throwable {
        List <Case> result = new ArrayList<Case>();
        result.add(new Case(1, UPRN, CASE_STATUS, CASE1_TYPEID, CASE_CREATEDDATE_TIMESTAMP, CASE_CREATEDBY, 
            CASE1_SAMPLEID, CASE1_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET));
        result.add(new Case(2, UPRN, CASE_STATUS, CASE2_TYPEID, CASE_CREATEDDATE_TIMESTAMP, CASE_CREATEDBY, 
            CASE2_SAMPLEID, CASE2_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET));
        result.add(new Case(3, UPRN, CASE_STATUS, CASE3_TYPEID, CASE_CREATEDDATE_TIMESTAMP, CASE_CREATEDBY, 
            CASE3_SAMPLEID, CASE3_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET));
      return result;
      }
    });

    Mockito.when(mockedService.findCasesByUprn(NON_EXISTING_ID)).thenAnswer(new Answer<List<Case>>() {
      public List<Case> answer(InvocationOnMock invocation)
              throws Throwable {
        return new ArrayList<Case>();
      }
    });

    Mockito.when(mockedService.findCaseByCaseId(CASEID)).thenAnswer(new Answer<Case>() {
      public Case answer(InvocationOnMock invocation)
          throws Throwable {
      return new Case(CASEID, UPRN, CASE_STATUS, CASE1_TYPEID, CASE_CREATEDDATE_TIMESTAMP, CASE_CREATEDBY, 
          CASE1_SAMPLEID, CASE1_ACTIONPLANID, CASE_SURVEYID, CASE_QUESTIONSET);
      }
    });

    Mockito.when(mockedService.findCaseByCaseId(NON_EXISTING_ID)).thenAnswer(new Answer<Case>() {
      public Case answer(InvocationOnMock invocation)
              throws Throwable {
        return null;
      }
    });

    
    return mockedService;
  }

  public void dispose(CaseService t) {
  }
}
