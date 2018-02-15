package uk.gov.ons.ctp.response.casesvc.state;

import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.state.BasicStateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

import java.util.HashMap;
import java.util.Map;

/**
 * This is the state transition manager actory for the actionsvc. It intended
 * that this will be refactored into a common framework class and that it
 * initialises each entities manager from database held transitions.
 */
@Component
public class CaseSvcStateTransitionManagerFactory implements StateTransitionManagerFactory {

  public static final String CASE_ENTITY = "Case";
  public static final String CASE_GROUP = "CaseGroup";

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
    transitionMapForActionable.put(CaseEvent.ACCOUNT_CREATED, CaseState.ACTIONABLE);
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

    //CASE GROUP TRANSITIONS
    Map<CaseGroupStatus, Map<CategoryDTO.CategoryName, CaseGroupStatus>> caseGroupTransitions = new HashMap<>();

    //Transition from NOTSTARTED TO INPROGRESS
    //TODO: Update with transitions for EQ's and Non-seft surveys
    Map<CategoryDTO.CategoryName, CaseGroupStatus> transitionMapForCaseStarted = new HashMap<>();
    transitionMapForCaseStarted.put(CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
            CaseGroupStatus.INPROGRESS);
    transitionMapForCaseStarted.put(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD,
            CaseGroupStatus.COMPLETE);
    caseGroupTransitions.put(CaseGroupStatus.NOTSTARTED, transitionMapForCaseStarted);

    //Transition from INPROGRESS to COMPLETED
    //TODO: Update with transitions for EQ's and Non-seft surveys
    Map<CategoryDTO.CategoryName, CaseGroupStatus> transitionMapForCaseInProgress = new HashMap<>();
    transitionMapForCaseInProgress.put(CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, CaseGroupStatus.COMPLETE);
    caseGroupTransitions.put(CaseGroupStatus.INPROGRESS, transitionMapForCaseInProgress);

    StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStatusTransitionManager =
            new BasicStateTransitionManager<>(caseGroupTransitions);

    managers.put(CASE_GROUP, caseGroupStatusTransitionManager);
  }

  @SuppressWarnings("unchecked")
  @Override
  public StateTransitionManager<?, ?> getStateTransitionManager(String entity) {
    return managers.get(entity);
  }

}
