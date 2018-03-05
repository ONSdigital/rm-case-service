package uk.gov.ons.ctp.response.casesvc.state;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

import java.util.ArrayList;
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
  private Map<CaseState, Map<CaseEvent, CaseState>> validCaseTransitions = new HashMap<>();
  private Map<CaseGroupStatus, Map<CategoryDTO.CategoryName, CaseGroupStatus>> validCaseGroupTransitions = new HashMap<>();

  /**
   * Setup the transitions
   */
  @BeforeClass
  public void setup() {
    populateCaseTransitions();
    populateCaseGroupStatusTransitions();
  }

  private void populateCaseTransitions() {
    Map<CaseEvent, CaseState> sampledInitTransitions = new HashMap<>();
    sampledInitTransitions.put(CaseEvent.ACTIVATED, CaseState.ACTIONABLE);
    validCaseTransitions.put(CaseState.SAMPLED_INIT, sampledInitTransitions);

    Map<CaseEvent, CaseState> replacementInitTransitions = new HashMap<>();
    replacementInitTransitions.put(CaseEvent.REPLACED, CaseState.ACTIONABLE);
    validCaseTransitions.put(CaseState.REPLACEMENT_INIT, replacementInitTransitions);

    Map<CaseEvent, CaseState> actionableTransitions = new HashMap<>();
    actionableTransitions.put(CaseEvent.ACCOUNT_CREATED, CaseState.ACTIONABLE);
    actionableTransitions.put(CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    actionableTransitions.put(CaseEvent.DISABLED, CaseState.INACTIONABLE);
    validCaseTransitions.put(CaseState.ACTIONABLE, actionableTransitions);

    Map<CaseEvent, CaseState> inactionableTransitions = new HashMap<>();
    inactionableTransitions.put(CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    inactionableTransitions.put(CaseEvent.DISABLED, CaseState.INACTIONABLE);
    validCaseTransitions.put(CaseState.INACTIONABLE, inactionableTransitions);
  }

  private void populateCaseGroupStatusTransitions() {
    //transitions from not started to in progress
    Map<CategoryDTO.CategoryName, CaseGroupStatus> caseNotStartedTransitions = new HashMap<>();
    caseNotStartedTransitions.put(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED, CaseGroupStatus.INPROGRESS);
    caseNotStartedTransitions.put(CategoryDTO.CategoryName.EQ_LAUNCH, CaseGroupStatus.INPROGRESS);
    caseNotStartedTransitions.put(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, CaseGroupStatus.COMPLETE);
    validCaseGroupTransitions.put(CaseGroupStatus.NOTSTARTED, caseNotStartedTransitions);

    //transitions from inprogress to completed
    Map<CategoryDTO.CategoryName, CaseGroupStatus> caseInProgressTransitions = new HashMap<>();
    caseInProgressTransitions.put(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, CaseGroupStatus.COMPLETE);
    validCaseGroupTransitions.put(CaseGroupStatus.INPROGRESS, caseInProgressTransitions);

  }

  /**
   * test a valid transition
   */
  @Test(threadPoolSize = THREAD_POOL_SIZE, invocationCount = INVOCATIONS, timeOut = TIMEOUT)
  public void testCaseTransitions() {
    StateTransitionManagerFactory stmFactory = new CaseSvcStateTransitionManagerFactory();
    StateTransitionManager<CaseState, CaseEvent> stm = stmFactory.getStateTransitionManager(
            CaseSvcStateTransitionManagerFactory.CASE_ENTITY);

    validCaseTransitions.forEach((sourceState, transitions) -> {
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

  @Test
  public void testCaseGroupTransitions() {
    StateTransitionManagerFactory factory = new CaseSvcStateTransitionManagerFactory();
    StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStageManager =
            factory.getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_GROUP);

    validCaseGroupTransitions.forEach((currentState, transitions) -> {
      transitions.forEach((caseEvent, caseState) ->{
        try {
          CaseGroupStatus newStatus = caseGroupStageManager.transition(currentState, caseEvent);
          assertEquals(caseState, newStatus);
        } catch (CTPException e) {
          fail();
        }
      });

      Arrays.asList(CategoryDTO.CategoryName.values()).forEach(event -> {
        if(!transitions.keySet().contains(event)) {
          try {
            //invalid state transition should fail
            CaseGroupStatus newStatus = caseGroupStageManager.transition(currentState, event);
            fail();
          } catch (CTPException ste) {
            assertEquals(String.format(TRANSITION_ERROR_MSG, currentState, event), ste.getMessage());
          }
        }
      });
    });
  }
}
