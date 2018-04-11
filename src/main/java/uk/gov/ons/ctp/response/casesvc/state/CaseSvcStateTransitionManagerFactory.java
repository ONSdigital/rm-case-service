package uk.gov.ons.ctp.response.casesvc.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.state.BasicStateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

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

  private final Map<String, StateTransitionManager<?, ?>> managers = ImmutableMap.<String, StateTransitionManager<?, ?>>builder()
          .put(CASE_ENTITY, caseStateTransitionManager())
          .put(CASE_GROUP, caseGroupStateTransitionManager())
          .build();

  private StateTransitionManager<CaseState, CaseEvent> caseStateTransitionManager() {
    ImmutableTable.Builder<CaseState, CaseEvent, CaseState> builder = ImmutableTable.builder();

    // From sample init on activated to actionable
    builder.put(CaseState.SAMPLED_INIT, CaseEvent.ACTIVATED, CaseState.ACTIONABLE);

    // From replacement init on replaced to actionable
    builder.put(CaseState.REPLACEMENT_INIT, CaseEvent.REPLACED, CaseState.ACTIONABLE);

    // From actionable on account created, deactivated, disabled to actionable, inactionable
    builder.put(CaseState.ACTIONABLE, CaseEvent.ACCOUNT_CREATED, CaseState.ACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DISABLED, CaseState.INACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DISABLE_RESPONDENT_ENROLMENT, CaseState.INACTIONABLE);

    // From inactionable on deactivated, disabled to inactionable
    builder.put(CaseState.INACTIONABLE, CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.DISABLED, CaseState.INACTIONABLE);

    return new BasicStateTransitionManager<>(builder.build().rowMap());
  }

  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStateTransitionManager() {
    ImmutableTable.Builder<CaseGroupStatus, CategoryDTO.CategoryName, CaseGroupStatus> builder = ImmutableTable.builder();

    // From not started on ci downloaded, eq launch, successful response upload, completed by phone to in progress, in progress, completed, completed by phone
    builder.put(CaseGroupStatus.NOTSTARTED, CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED, CaseGroupStatus.INPROGRESS);
    builder.put(CaseGroupStatus.NOTSTARTED, CategoryDTO.CategoryName.EQ_LAUNCH, CaseGroupStatus.INPROGRESS);
    builder.put(CaseGroupStatus.NOTSTARTED, CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, CaseGroupStatus.COMPLETE);
    builder.put(CaseGroupStatus.NOTSTARTED, CategoryDTO.CategoryName.COMPLETED_BY_PHONE, CaseGroupStatus.COMPLETEDBYPHONE);
    
    // From in progress on response processed by SDX to complete   
    builder.put(CaseGroupStatus.INPROGRESS, CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED, CaseGroupStatus.COMPLETE);

    // From in progress on successful response upload, completed by phone to completed, completed by phone
    builder.put(CaseGroupStatus.INPROGRESS, CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD, CaseGroupStatus.COMPLETE);
    builder.put(CaseGroupStatus.INPROGRESS, CategoryDTO.CategoryName.COMPLETED_BY_PHONE, CaseGroupStatus.COMPLETEDBYPHONE);

    // From reopened on completed by phone to completed by phone
    builder.put(CaseGroupStatus.REOPENED, CategoryDTO.CategoryName.COMPLETED_BY_PHONE, CaseGroupStatus.COMPLETEDBYPHONE);

    return new BasicStateTransitionManager<>(builder.build().rowMap());
  }

  @SuppressWarnings("unchecked")
  @Override
  public StateTransitionManager<?, ?> getStateTransitionManager(String entity) {
    return managers.get(entity);
  }

}
