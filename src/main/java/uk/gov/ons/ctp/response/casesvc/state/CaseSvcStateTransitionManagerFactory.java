package uk.gov.ons.ctp.response.casesvc.state;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableTable;
import java.util.Map;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.common.state.BasicStateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.common.state.StateTransitionManagerFactory;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

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

    // From actionable on actionplan changed, deactivated, disabled to actionable, inactionable
    builder.put(CaseState.ACTIONABLE, CaseEvent.ACTIONPLAN_CHANGED, CaseState.ACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    builder.put(CaseState.ACTIONABLE, CaseEvent.DISABLED, CaseState.INACTIONABLE);

    // From inactionable on actionplan changed, deactivated, disabled to inactionable
    builder.put(CaseState.INACTIONABLE, CaseEvent.ACTIVATED, CaseState.ACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.ACTIONPLAN_CHANGED, CaseState.INACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.DEACTIVATED, CaseState.INACTIONABLE);
    builder.put(CaseState.INACTIONABLE, CaseEvent.DISABLED, CaseState.INACTIONABLE);

    return new BasicStateTransitionManager<>(builder.build().rowMap());
  }

  private StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>
      caseGroupStateTransitionManager() {
    ImmutableTable.Builder<CaseGroupStatus, CategoryDTO.CategoryName, CaseGroupStatus> builder =
        ImmutableTable.builder();

    // From not started on ci downloaded, eq launch, successful response upload, completed by phone
    // to in progress, in progress, completed, completed by phone, no longer required
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED,
        CaseGroupStatus.INPROGRESS);
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
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.LEGITIMACY_CONCERNS,
        CaseGroupStatus.REFUSAL);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.OTHER_OUTRIGHT_REFUSAL,
        CaseGroupStatus.REFUSAL);

    // Transitions to refusal
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
        CaseGroupStatus.REFUSAL);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.LEGITIMACY_CONCERNS,
        CaseGroupStatus.REFUSAL);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.OTHER_OUTRIGHT_REFUSAL,
        CaseGroupStatus.REFUSAL);

    // Transitions to refusal
    // From complete
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
        CaseGroupStatus.REFUSAL);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.LEGITIMACY_CONCERNS,
        CaseGroupStatus.REFUSAL);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.OTHER_OUTRIGHT_REFUSAL,
        CaseGroupStatus.REFUSAL);

    // Transitions to Other non-response
    // From not started
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.ILL_AT_HOME,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.IN_HOSPITAL,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.LANGUAGE_DIFFICULTIES,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.TOO_BUSY,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.OTHER_CIRCUMSTANTIAL_REFUSAL,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT,
        CaseGroupStatus.OTHERNONRESPONSE);

    // Transitions to Other non-response
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.PARTIAL_INTERVIEW_REQUEST_DATA_DELETED,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.PARTIAL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.ILL_AT_HOME,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.IN_HOSPITAL,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.LANGUAGE_DIFFICULTIES,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.TOO_BUSY,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.OTHER_CIRCUMSTANTIAL_REFUSAL,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT,
        CaseGroupStatus.OTHERNONRESPONSE);

    // Transitions to Other non-response
    // From complete
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.FULL_INTERVIEW_REQUEST_DATA_DELETED,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.FULL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.ILL_AT_HOME,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.IN_HOSPITAL,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.LANGUAGE_DIFFICULTIES,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.TOO_BUSY,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.OTHER_CIRCUMSTANTIAL_REFUSAL,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE,
        CaseGroupStatus.OTHERNONRESPONSE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT,
        CaseGroupStatus.OTHERNONRESPONSE);

    // Transitions to unknown eligibility
    // From not started
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS,
        CaseGroupStatus.UNKNOWNELIGIBILITY);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.WRONG_ADDRESS,
        CaseGroupStatus.UNKNOWNELIGIBILITY);

    // Transitions to unknown eligibility
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS,
        CaseGroupStatus.UNKNOWNELIGIBILITY);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.WRONG_ADDRESS,
        CaseGroupStatus.UNKNOWNELIGIBILITY);

    // Transitions to unknown eligibility
    // From complete
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS,
        CaseGroupStatus.UNKNOWNELIGIBILITY);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.WRONG_ADDRESS,
        CaseGroupStatus.UNKNOWNELIGIBILITY);

    // Transitions to not eligible
    // From not started
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.VACANT_OR_EMPTY,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.NON_RESIDENTIAL_ADDRESS,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.COMMUNAL_ESTABLISHMENT_INSTITUTION,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.NOTSTARTED,
        CategoryDTO.CategoryName.NO_PERSON_IN_ELIGIBLE_AGE_RANGE,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.NOTSTARTED, CategoryDTO.CategoryName.DECEASED, CaseGroupStatus.NOTELIGIBLE);

    // Transitions to not eligible
    // From in progress
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.VACANT_OR_EMPTY,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.NON_RESIDENTIAL_ADDRESS,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.COMMUNAL_ESTABLISHMENT_INSTITUTION,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.INPROGRESS,
        CategoryDTO.CategoryName.NO_PERSON_IN_ELIGIBLE_AGE_RANGE,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.INPROGRESS, CategoryDTO.CategoryName.DECEASED, CaseGroupStatus.NOTELIGIBLE);

    // Transitions to not eligible
    // From in progress
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.VACANT_OR_EMPTY,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.NON_RESIDENTIAL_ADDRESS,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.COMMUNAL_ESTABLISHMENT_INSTITUTION,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.COMPLETE,
        CategoryDTO.CategoryName.NO_PERSON_IN_ELIGIBLE_AGE_RANGE,
        CaseGroupStatus.NOTELIGIBLE);
    builder.put(
        CaseGroupStatus.COMPLETE, CategoryDTO.CategoryName.DECEASED, CaseGroupStatus.NOTELIGIBLE);

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

    // From in progress on successful response upload, completed by phone to completed, completed by
    // phone, no longer required
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

    return new BasicStateTransitionManager<>(builder.build().rowMap());
  }

  @SuppressWarnings("unchecked")
  @Override
  public StateTransitionManager<?, ?> getStateTransitionManager(String entity) {
    return managers.get(entity);
  }
}
