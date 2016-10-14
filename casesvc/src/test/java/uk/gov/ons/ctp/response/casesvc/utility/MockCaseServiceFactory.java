package uk.gov.ons.ctp.response.casesvc.utility;

import static org.mockito.Matchers.any;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.service.CaseService;

/**
 * Mock CaseService response HK2 JSE JSR-330 dependency injection factory
 */
public final class MockCaseServiceFactory implements Factory<CaseService> {

  public static final CaseDTO.CaseState CASE_STATE = CaseDTO.CaseState.SAMPLED_INIT;
  public static final Integer CASE1_ACTIONPLANMAPPINGID = 1;
  public static final Integer CASE1_TYPEID = 1;
  public static final Integer CASE2_TYPEID = 2;
  public static final Integer CASE3_TYPEID = 3;
  public static final String CREATEDDATE_VALUE = "2016-04-15T16:02:39.699+0000";
  public static final Timestamp CREATEDDATE_TIMESTAMP = Timestamp.valueOf("2016-02-26 18:30:00");
  public static final String CREATEDBY = "UnitTester";
  public static final Integer EXISTING_ID = 991;
  public static final Integer NON_EXISTING_ID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final Integer CASEID = 124;
  public static final Integer QUESTIONNAIREID = 1234567907;
  public static final String CASEEVENT_DESC1 = "Test Event 1";
  public static final String CASEEVENT_DESC2 = "Test Event 2";
  public static final String CASEEVENT_DESC3 = "Test Event 3";
  public static final CategoryDTO.CategoryType CASEEVENT_CATEGORY = CategoryDTO.CategoryType.GENERAL_ENQUIRY;
  public static final String CASEEVENT_SUBCATEGORY = "subcat";

  /**
   * provide method
   *
   * @return mocked service
   */
  public CaseService provide() {

    final CaseService mockedService = Mockito.mock(CaseService.class);

    try {
      List<Case> cases = FixtureHelper.loadClassFixtures(Case[].class);
      List<CaseEvent> caseEvents = FixtureHelper.loadClassFixtures(CaseEvent[].class);

      Mockito.when(mockedService.findCaseByCaseId(CASEID)).thenAnswer(new Answer<Case>() {
        public Case answer(final InvocationOnMock invocation)
            throws Throwable {
          return cases.get(0);
        }
      });

      Mockito.when(mockedService.findCaseByCaseId(NON_EXISTING_ID)).thenAnswer(new Answer<Case>() {
        public Case answer(final InvocationOnMock invocation)
            throws Throwable {
          return null;
        }
      });

      Mockito.when(mockedService.findCaseEventsByCaseId(CASEID)).thenAnswer(new Answer<List<CaseEvent>>() {
        public List<CaseEvent> answer(final InvocationOnMock invocation)
            throws Throwable {
          return caseEvents;
        }
      });

      Mockito.when(mockedService.findCaseEventsByCaseId(NON_EXISTING_ID)).thenAnswer(new Answer<List<CaseEvent>>() {
        public List<CaseEvent> answer(final InvocationOnMock invocation)
            throws Throwable {
          return new ArrayList<CaseEvent>();
        }
      });

      Mockito.when(mockedService.findCaseByCaseId(UNCHECKED_EXCEPTION))
          .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

      Mockito.when(mockedService.createCaseEvent(any(CaseEvent.class))).thenAnswer(new Answer<CaseEvent>() {
        public CaseEvent answer(final InvocationOnMock invocation)
            throws Throwable {
          return caseEvents.get(0);
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
  public void dispose(final CaseService t) {
  }
}
