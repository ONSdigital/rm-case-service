package uk.gov.ons.ctp.response.casesvc.state;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Named;

import uk.gov.ons.ctp.common.state.BasicStateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseState;

/**
 * This is the state transition manager actory for the actionsvc. It intended
 * that this will be refactored into a common framework class and that it
 * initialises each entities manager from database held transitions.
 */
@Named
public class CaseSvcStateTransitionManagerFactory implements StateTransitionManagerFactory {

  public static final String CASE_ENTITY = "Case";

  private Map<String, StateTransitionManager<?, ?>> managers;

  /**
   * Create and init the factory with concrete StateTransitionManagers for each
   * required entity
   */
  public CaseSvcStateTransitionManagerFactory() {
    managers = new HashMap<>();

    Map<CaseState, Map<CaseEvent, CaseState>> transitions = new HashMap<>();

    Map<CaseEvent, CaseState> transitionMapForSampledInit = new HashMap<>();
    transitionMapForSampledInit.put(CaseEvent.ACTIVATED, CaseState.ACTIONABLE);
    transitions.put(CaseState.SAMPLED_INIT, transitionMapForSampledInit);

    Map<CaseEvent, CaseState> transitionMapForReplacementInit = new HashMap<>();
    transitionMapForReplacementInit.put(CaseEvent.REPLACED, CaseState.ACTIONABLE);
    transitions.put(CaseState.REPLACEMENT_INIT, transitionMapForReplacementInit);

    Map<CaseEvent, CaseState> transitionMapForActionable = new HashMap<>();
    transitionMapForActionable.put(CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    transitionMapForActionable.put(CaseEvent.DISABLED, CaseState.INACTIONABLE);
    transitions.put(CaseState.ACTIONABLE, transitionMapForActionable);

    Map<CaseEvent, CaseState> transitionMapForInactionable = new HashMap<>();
    transitionMapForInactionable.put(CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    transitionMapForInactionable.put(CaseEvent.DISABLED, CaseState.INACTIONABLE);
    transitions.put(CaseState.INACTIONABLE, transitionMapForInactionable);

    StateTransitionManager<CaseState, CaseEvent> caseStateTransitionManager =
        new BasicStateTransitionManager<>(transitions);

    managers.put(CASE_ENTITY, caseStateTransitionManager);

  }

  @SuppressWarnings("unchecked")
  @Override
  public StateTransitionManager<?, ?> getStateTransitionManager(String entity) {
    return managers.get(entity);
  }

}
