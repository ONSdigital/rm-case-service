package uk.gov.ons.ctp.response.casesvc.state;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseState;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertEquals;
import static uk.gov.ons.ctp.common.state.BasicStateTransitionManager.*;

/**
 * A test of the state transition manager It simply has to test a single good
 * and a single bad transition - all it is testing is the underlying mechanism,
 * not a real implementation, where we will want to assert all of the valid and
 * invalid transitions
 *
 */
public class StateTransitionManagerUnitTest {

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
    actionableTransitions.put(CaseEvent.ACCOUNT_CREATED, CaseState.ACTIONABLE);
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
   */
  @Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATIONS, timeOut = TIMEOUT)
  public void testCaseTransitions() {
    StateTransitionManagerFactory stmFactory = new CaseSvcStateTransitionManagerFactory();
    StateTransitionManager<CaseState, CaseEvent> stm = stmFactory.getStateTransitionManager(
            CaseSvcStateTransitionManagerFactory.CASE_ENTITY);

    validTransitions.forEach((sourceState, transitions) -> {
      transitions.forEach((caseEvent, caseState) -> {
        try {
          assertEquals(caseState, stm.transition(sourceState, caseEvent));
        } catch (CTPException e) {
          fail();
        }
      });

      Arrays.asList(CaseEvent.values()).forEach(event -> {
        if (!transitions.keySet().contains(event)) {
          try {
            stm.transition(sourceState, event);
            fail();
          } catch (CTPException ste) {
            assertEquals(String.format(TRANSITION_ERROR_MSG, sourceState, event), ste.getMessage());
          }
        }
      });
    });
  }
}
