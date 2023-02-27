package uk.gov.ons.ctp.response.casesvc.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.lib.common.state.BasicStateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManagerFactory;

/**
 * This is the state transition manager actory for the actionsvc. It intended that this will be
 * refactored into a common framework class and that it initialises each entities manager from
 * database held transitions.
 */
@Component
public class CaseSvcStateTransitionManagerFactory implements StateTransitionManagerFactory {

  public static final String CASE_ENTITY = "Case";
  public static final String CASE_GROUP = "CaseGroup";

  private final Map<String, StateTransitionManager<?, ?>> managers =
      ImmutableMap.<String, StateTransitionManager<?, ?>>builder()
          .put(CASE_ENTITY, caseStateTransitionManager())
          .put(CASE_GROUP, caseGroupStateTransitionManager())
          .build();

  private StateTransitionManager<CaseState, CaseEvent> caseStateTransitionManager() {
    ImmutableTable.Builder<CaseState, CaseEvent, CaseState> builder = ImmutableTable.builder();

    // From sample init on activated to actionable
    builder.put(CaseState.SAMPLED_INIT, CaseEvent.ACTIVATED, CaseState.ACTIONABLE);

    // From replacement init on replaced to actionable
    builder.put(CaseState.REPLACEMENT_INIT, CaseEvent.REPLACED, CaseState.ACTIONABLE);

    // From actionable on actionplan changed, deactivated, disabled to actionable,
    // inactionable
    builder.put(CaseState.ACTIONABLE, CaseEvent.ACTIONPLAN_CHANGED, CaseState.ACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DISABLED, CaseState.INACTIONABLE);

    // From inactionable on actionplan changed, deactivated, disabled to
    // inactionable
    builder.put(CaseState.INACTIONABLE, CaseEvent.ACTIVATED, CaseState.ACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.ACTIONPLAN_CHANGED, CaseState.INACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.DISABLED, CaseState.INACTIONABLE);

    // COMPLETED_TO_NOTSTARTED new event entry
    builder.put(CaseState.ACTIONABLE, CaseEvent.ACTIVATED, CaseState.ACTIONABLE);
    return new BasicStateTransitionManager<>(builder.build().rowMap());
  }

  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>
      caseGroupStateTransitionManager() {
    ImmutableTable.Builder<CaseGroupStatus, CategoryDTO.CategoryName, CaseGroupStatus> builder =
        ImmutableTable.builder();

    // NOT STARTED
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
        CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
        CaseGroupStatus.NOTSTARTED);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.RESPONDENT_ENROLED,
        CaseGroupStatus.NOTSTARTED);
    builder.put(
        CaseGroupStatus.NOTSTARTED, CategoryDTO.CategoryName.EQ_LAUNCH, CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD,
        CaseGroupStatus.COMPLETE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.COMPLETED_BY_PHONE,
        CaseGroupStatus.COMPLETEDBYPHONE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.NO_LONGER_REQUIRED,
        CaseGroupStatus.NOLONGERREQUIRED);

    // Transitions to refusal
    // From not started
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
        CaseGroupStatus.REFUSAL);

    // Transitions to refusal
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
        CaseGroupStatus.REFUSAL);

    // Transitions to refusal
    // From complete
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
        CaseGroupStatus.REFUSAL);

    // Transitions to Other non-response
    // From not started
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE,
        CaseGroupStatus.OTHERNONRESPONSE);

    // Transitions to Other non-response
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE,
        CaseGroupStatus.OTHERNONRESPONSE);

    // Transitions to Other non-response
    // From complete
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE,
        CaseGroupStatus.OTHERNONRESPONSE);

    // Transitions to not eligible
    // From not started
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT,
        CaseGroupStatus.NOTELIGIBLE);

    // Transitions to not eligible
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT,
        CaseGroupStatus.NOTELIGIBLE);

    // Transitions to not eligible
    // From in progress
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT,
        CaseGroupStatus.NOTELIGIBLE);

    // Transitions from social outcomes to in progress
    builder.put(
        CaseGroupStatus.REFUSAL, CategoryDTO.CategoryName.EQ_LAUNCH, CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.NOTELIGIBLE,
        CategoryDTO.CategoryName.EQ_LAUNCH,
        CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.OTHERNONRESPONSE,
        CategoryDTO.CategoryName.EQ_LAUNCH,
        CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.UNKNOWNELIGIBILITY,
        CategoryDTO.CategoryName.EQ_LAUNCH,
        CaseGroupStatus.INPROGRESS);

    // Transitions from social outcomes to complete
    builder.put(
        CaseGroupStatus.REFUSAL,
        CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
        CaseGroupStatus.COMPLETE);
    builder.put(
        CaseGroupStatus.NOTELIGIBLE,
        CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
        CaseGroupStatus.COMPLETE);
    builder.put(
        CaseGroupStatus.OTHERNONRESPONSE,
        CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
        CaseGroupStatus.COMPLETE);
    builder.put(
        CaseGroupStatus.UNKNOWNELIGIBILITY,
        CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
        CaseGroupStatus.COMPLETE);

    // From in progress on response processed by SDX to complete
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED,
        CaseGroupStatus.COMPLETE);

    // From in progress on successful response upload, completed by phone to
    // completed, completed by
    // phone, no longer required
    builder.put(
        CaseGroupStatus.INPROGRESS, CategoryDTO.CategoryName.EQ_LAUNCH, CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
        CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
        CaseGroupStatus.INPROGRESS);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD,
        CaseGroupStatus.COMPLETE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.COMPLETED_BY_PHONE,
        CaseGroupStatus.COMPLETEDBYPHONE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.NO_LONGER_REQUIRED,
        CaseGroupStatus.NOLONGERREQUIRED);

    // From reopened on completed by phone to completed by phone
    builder.put(
        CaseGroupStatus.REOPENED,
        CategoryDTO.CategoryName.COMPLETED_BY_PHONE,
        CaseGroupStatus.COMPLETEDBYPHONE);
    builder.put(
        CaseGroupStatus.REOPENED,
        CategoryDTO.CategoryName.NO_LONGER_REQUIRED,
        CaseGroupStatus.NOLONGERREQUIRED);

    // New transition to enable users to change completed by phone and no longer required to not
    // started
    builder.put(
        CaseGroupStatus.COMPLETEDBYPHONE,
        CategoryDTO.CategoryName.COMPLETED_TO_NOTSTARTED,
        CaseGroupStatus.NOTSTARTED);
    builder.put(
        CaseGroupStatus.NOLONGERREQUIRED,
        CategoryDTO.CategoryName.COMPLETED_TO_NOTSTARTED,
        CaseGroupStatus.NOTSTARTED);

    // In response-operations-ui, looking at the cases for a survey also shows you an unused IAC
    // code.  By viewing this
    // an ACCESS_CODE_AUTHENTICATION_ATTEMPT event happens.  If the respondent has completed the
    // survey then we
    // shouldn't throw a warning just because we wanted to look at a completely valid page.
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.ACCESS_CODE_AUTHENTICATION_ATTEMPT,
        CaseGroupStatus.COMPLETE);

    return new BasicStateTransitionManager<>(builder.build().rowMap());
  }

  @SuppressWarnings("unchecked")
  @Override
  public StateTransitionManager<?, ?> getStateTransitionManager(String entity) {
    return managers.get(entity);
  }
}
