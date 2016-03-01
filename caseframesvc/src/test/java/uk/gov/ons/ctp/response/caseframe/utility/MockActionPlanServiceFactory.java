package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.ActionPlan;
import uk.gov.ons.ctp.response.caseframe.service.ActionPlanService;

/**
 * Created by Martin.Humphrey on 26/2/2016.
 */
public class MockActionPlanServiceFactory implements Factory<ActionPlanService> {

  public static final String ACTIONPLAN1_NAME = "HH_APL1";
  public static final String ACTIONPLAN2_NAME = "HGH_APL1";
  public static final String ACTIONPLAN3_NAME = "CH_APL1";
  public static final String ACTIONPLAN1_DESC = "Household Action Plan 1";
  public static final String ACTIONPLAN2_DESC = "Hotel and Guest House Action Plan 1";
  public static final String ACTIONPLAN3_DESC = "Care Home Action Plan 1";
  public static final String ACTIONPLAN1_RULES = "IAC letter";
  public static final String ACTIONPLAN2_RULES = "visit";
  public static final String ACTIONPLAN3_RULES = "IAC letter";
  public static final Integer ACTIONPLANID = 3;
  public static final Integer NON_EXISTING_ACTIONPLANID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  public ActionPlanService provide() {

    final ActionPlanService mockedService = Mockito.mock(ActionPlanService.class);

    Mockito.when(mockedService.findActionPlans()).thenAnswer(new Answer<List<ActionPlan>>() {
      public List<ActionPlan> answer(InvocationOnMock invocation)
          throws Throwable {
        List <ActionPlan> result = new ArrayList<ActionPlan>();
        result.add(new ActionPlan(1, ACTIONPLAN1_NAME, ACTIONPLAN1_DESC, ACTIONPLAN1_RULES));
        result.add(new ActionPlan(2, ACTIONPLAN2_NAME, ACTIONPLAN2_DESC, ACTIONPLAN2_RULES));
        result.add(new ActionPlan(3, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC, ACTIONPLAN3_RULES));
      return result;
      }
    });

    Mockito.when(mockedService.findActionPlan(ACTIONPLANID)).thenAnswer(new Answer<ActionPlan>() {
      public ActionPlan answer(InvocationOnMock invocation)
          throws Throwable {
      return new ActionPlan(3, ACTIONPLAN3_NAME, ACTIONPLAN3_DESC, ACTIONPLAN3_RULES);
      }
    });

    Mockito.when(mockedService.findActionPlan(UNCHECKED_EXCEPTION)).thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findActionPlan(NON_EXISTING_ACTIONPLANID)).thenAnswer(new Answer<ActionPlan>() {
      public ActionPlan answer(InvocationOnMock invocation)
              throws Throwable {
        return null;
      }
    });

    return mockedService;
  }

  public void dispose(ActionPlanService t) {
  }
}