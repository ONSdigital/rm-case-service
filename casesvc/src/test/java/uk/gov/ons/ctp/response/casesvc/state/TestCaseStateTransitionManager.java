package uk.gov.ons.ctp.response.casesvc.state;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseState;

/**
 * A test of the state transition manager It simply has to test a single good
 * and a single bad transition - all it is testing is the underlying mechanism,
 * not a real implementation, where we will want to assert all of the valid and
 * invalid transitions
 *
 */
public class TestCaseStateTransitionManager {

  private static final int TIMEOUT = 10000;
  private static final int INVOCATIONS = 50;
  private static final int THREAD_POOL_SIZE = 10;
  private Map<CaseState, Map<CaseEvent, CaseState>> validTransitions = new HashMap<>();

  /**
   * Setup the transitions
   */
  @BeforeClass
  public void setup() {
    Map<CaseEvent, CaseState> sampledInitTransitions = new HashMap<>();
    sampledInitTransitions.put(CaseEvent.ACTIVATED, CaseState.ACTIONABLE);
    validTransitions.put(CaseState.SAMPLED_INIT, sampledInitTransitions);

    Map<CaseEvent, CaseState> replacementInitTransitions = new HashMap<>();
    replacementInitTransitions.put(CaseEvent.REPLACED, CaseState.ACTIONABLE);
    validTransitions.put(CaseState.REPLACEMENT_INIT, replacementInitTransitions);
    
    Map<CaseEvent, CaseState> actionableTransitions = new HashMap<>();
    actionableTransitions.put(CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    actionableTransitions.put(CaseEvent.DISABLED, CaseState.INACTIONABLE);
    validTransitions.put(CaseState.ACTIONABLE, actionableTransitions);

    Map<CaseEvent, CaseState> inactionableTransitions = new HashMap<>();
    inactionableTransitions.put(CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    inactionableTransitions.put(CaseEvent.DISABLED, CaseState.INACTIONABLE);
    validTransitions.put(CaseState.INACTIONABLE, inactionableTransitions);
  }

  /**
   * test a valid transition
   *
   * @throws StateTransitionException shouldn't!
   */
  @Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATIONS, timeOut = TIMEOUT)
  public void testCaseTransitions() {
    StateTransitionManagerFactory stmFactory = new CaseSvcStateTransitionManagerFactory();
    StateTransitionManager<CaseState, CaseEvent> stm = stmFactory
        .getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_ENTITY);

    validTransitions.forEach((sourceState, transitions) -> {
      transitions.forEach((caseEvent, caseState) -> {
        try {
          Assert.assertEquals(caseState, stm.transition(sourceState, caseEvent));
        } catch (RuntimeException ste) {
          Assert.fail("bad transition!", ste);
        }
      });

      Arrays.asList(CaseEvent.values()).forEach(event -> {
        if (!transitions.keySet().contains(event)) {
          boolean caught = false;
          try {
            stm.transition(sourceState, event);
          } catch (RuntimeException ste) {
            caught = true;
          }
          Assert.assertTrue(caught, "Transition " + sourceState + "(" + event + ") should be invalid");
        }
      });
    });
  }
}
