package uk.gov.ons.ctp.response.casesvc.state;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

public class StateTransitionManagerUnitTest {

  private final CaseSvcStateTransitionManagerFactory factory =
      new CaseSvcStateTransitionManagerFactory();

  @SuppressWarnings("unchecked")
  private final StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseStateMachine =
      (StateTransitionManager<CaseState, CaseDTO.CaseEvent>)
          factory.getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_ENTITY);

  @SuppressWarnings("unchecked")
  private final StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>
      caseGroupStateMachine =
          (StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>)
              factory.getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_GROUP);

  @Test
  public void givenCaseStateSampledInitWhenActivatedThenActionable() throws CTPException {
    // Given
    CaseState sampledInit = CaseState.SAMPLED_INIT;

    // When
    CaseState destinationState =
        caseStateMachine.transition(sampledInit, CaseDTO.CaseEvent.ACTIVATED);

    // Then
    assertEquals(destinationState, CaseState.ACTIONABLE);
  }

  @Test
  public void givenCaseStateReplacementInitWhenReplacedThenActionable() throws CTPException {
    // Given
    CaseState replacementInit = CaseState.REPLACEMENT_INIT;

    // When
    CaseState destinationState =
        caseStateMachine.transition(replacementInit, CaseDTO.CaseEvent.REPLACED);

    // Then
    assertEquals(destinationState, CaseState.ACTIONABLE);
  }

  @Test
  public void givenCaseStateActionableWhenDisabledThenInactionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(actionable, CaseDTO.CaseEvent.DISABLED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseStateActionableWhenDeactivatedThenInactionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(actionable, CaseDTO.CaseEvent.DEACTIVATED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseStateInactionableWhenDisabledThenInactionable() throws CTPException {
    // Given
    CaseState inactionable = CaseState.INACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(inactionable, CaseDTO.CaseEvent.DISABLED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseStateInactionableWhenDeactivatedThenInactionable() throws CTPException {
    // Given
    CaseState inactionable = CaseState.INACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(inactionable, CaseDTO.CaseEvent.DEACTIVATED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenCIDownloadedThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED);

    // Then
    assertEquals(destinationState, CaseGroupStatus.INPROGRESS);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenEQLaunchThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.EQ_LAUNCH);

    // Then
    assertEquals(destinationState, CaseGroupStatus.INPROGRESS);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenSuccessfulResponseUploadedThenComplete()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETE);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenCompletedByPhoneThenCompletedByPhone()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.COMPLETED_BY_PHONE);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETEDBYPHONE);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenNoLongerRequiredThenNoLongerRequired()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.NO_LONGER_REQUIRED);

    // Then
    assertEquals(destinationState, CaseGroupStatus.NOLONGERREQUIRED);
  }

  @Test
  public void givenCaseGroupStateInProgressWhenSuccessfulResponseUploadThenComplete()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETE);
  }

  @Test
  public void givenCaseGroupStateInProgressWhenCompletedByPhoneThenCompletedByPhone()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.COMPLETED_BY_PHONE);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETEDBYPHONE);
  }

  @Test
  public void givenCaseGroupStateInProgressWhenNoLongerRequiredThenNoLongerRequired()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.NO_LONGER_REQUIRED);

    // Then
    assertEquals(destinationState, CaseGroupStatus.NOLONGERREQUIRED);
  }

  @Test
  public void givenCaseGroupStateReopenedWhenCompletedByPhoneThenCompletedByPhone()
      throws CTPException {
    // Given
    CaseGroupStatus reopened = CaseGroupStatus.REOPENED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(reopened, CategoryDTO.CategoryName.COMPLETED_BY_PHONE);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETEDBYPHONE);
  }

  @Test
  public void givenCaseGroupStateReopenedWhenNoLongerRequiredThenNoLongerRequired()
      throws CTPException {
    // Given
    CaseGroupStatus reopened = CaseGroupStatus.REOPENED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(reopened, CategoryDTO.CategoryName.NO_LONGER_REQUIRED);

    // Then
    assertEquals(destinationState, CaseGroupStatus.NOLONGERREQUIRED);
  }

  // Social Outcomes

  // Not started to refusal
  @Test
  public void givenNotStartedWhenPrivacyDataConfidentialityConcernsThenRefusal()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  @Test
  public void givenNotStartedWhenLegitimacyConcernsThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.LEGITIMACY_CONCERNS);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  @Test
  public void givenNotStartedWhenOtherOutrightRefusalThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.OTHER_OUTRIGHT_REFUSAL);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  // In progress to refusal
  @Test
  public void givenInProgressWhenPrivacyDataConfidentialityConcernsThenRefusal()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  @Test
  public void givenInProgressWhenLegitimacyConcernsThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.LEGITIMACY_CONCERNS);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  @Test
  public void givenInProgressWhenOtherOutrightRefusalThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.OTHER_OUTRIGHT_REFUSAL);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  // Complete to refusal
  @Test
  public void givenCompleteWhenPrivacyDataConfidentialityConcernsThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.PRIVACY_DATA_CONFIDENTIALITY_CONCERNS);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  @Test
  public void givenCompleteWhenLegitimacyConcernsThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.LEGITIMACY_CONCERNS);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  @Test
  public void givenCompleteWhenOtherOutrightRefusalThenRefusal() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.OTHER_OUTRIGHT_REFUSAL);

    // Then
    assertEquals(CaseGroupStatus.REFUSAL, destinationState);
  }

  // Not started to other non response
  @Test
  public void givenNotStartedWhenIllAtHomeThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.ILL_AT_HOME);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenInHospitalThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.IN_HOSPITAL);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenPhysicallyOrMentallyUnableThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenLanguageDifficultiesThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.LANGUAGE_DIFFICULTIES);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenLackOfComputerOrInternetAccessThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenTooBusyThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.TOO_BUSY);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenOtherCircumstantialThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.OTHER_CIRCUMSTANTIAL_REFUSAL);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenComplyInDifferentCollectionModeThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenNotStartedWhenRequestToCompleteInAlternativeFormatThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  // In progress to other non response
  @Test
  public void givenInProgressWhenPartialInterviewRequestDataDeletedThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.PARTIAL_INTERVIEW_REQUEST_DATA_DELETED);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenPartialInterviewRequestDataDeletedAsIncorrectThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.PARTIAL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenIllAtHomeThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.ILL_AT_HOME);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenInHospitalThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.IN_HOSPITAL);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenPhysicallyOrMentallyUnableThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenLanguageDifficultiesThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.LANGUAGE_DIFFICULTIES);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenLackOfComputerOrInternetAccessThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenTooBusyThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.TOO_BUSY);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenOtherCircumstantialThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.OTHER_CIRCUMSTANTIAL_REFUSAL);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenComplyInDifferentCollectionModeThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenInProgressWhenRequestToCompleteInAlternativeFormatThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  // Complete to other non response
  @Test
  public void givenCompleteWhenFullInterviewRequestDataDeletedThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.FULL_INTERVIEW_REQUEST_DATA_DELETED);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenFullInterviewRequestDataDeletedAsIncorrectThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.FULL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenIllAtHomeThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.ILL_AT_HOME);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenInHospitalThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.IN_HOSPITAL);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenPhysicallyOrMentallyUnableThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.PHYSICALLY_OR_MENTALLY_UNABLE);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenLanguageDifficultiesThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.LANGUAGE_DIFFICULTIES);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenLackOfComputerOrInternetAccessThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.LACK_OF_COMPUTER_INTERNET_ACCESS);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenTooBusyThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.TOO_BUSY);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenOtherCircumstantialThenOtherNonResponse() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.OTHER_CIRCUMSTANTIAL_REFUSAL);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenComplyInDifferentCollectionModeThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.COMPLY_IN_DIFFERENT_COLLECTION_MODE);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  @Test
  public void givenCompleteWhenRequestToCompleteInAlternativeFormatThenOtherNonResponse()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT);

    // Then
    assertEquals(CaseGroupStatus.OTHERNONRESPONSE, destinationState);
  }

  // Not started to unknown eligibility
  @Test
  public void givenNotStartedWhenNoTraceOfAddressThenUnknownEligibility() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.UNKNOWNELIGIBILITY, destinationState);
  }

  @Test
  public void givenNotStartedWhenWrongAddressThenUnknownEligibility() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.WRONG_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.UNKNOWNELIGIBILITY, destinationState);
  }

  // In progress to unknown eligibility
  @Test
  public void givenInProgressWhenNoTraceOfAddressThenUnknownEligibility() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.UNKNOWNELIGIBILITY, destinationState);
  }

  @Test
  public void givenInProgressWhenWrongAddressThenUnknownEligibility() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.WRONG_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.UNKNOWNELIGIBILITY, destinationState);
  }

  // Complete to unknown eligibility
  @Test
  public void givenCompleteWhenNoTraceOfAddressThenUnknownEligibility() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.NO_TRACE_OF_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.UNKNOWNELIGIBILITY, destinationState);
  }

  @Test
  public void givenCompleteWhenWrongAddressThenUnknownEligibility() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.WRONG_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.UNKNOWNELIGIBILITY, destinationState);
  }

  // Not started to not eligible
  @Test
  public void givenNotStartedWhenVacantOrEmptyThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.VACANT_OR_EMPTY);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenNotStartedWhenNonResidentialAddressThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.NON_RESIDENTIAL_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenNotStartedWhenAddressOccupiedButNoResidentThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenNotStartedWhenCommunalEstablishmentInstitutionThenNotEligible()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.COMMUNAL_ESTABLISHMENT_INSTITUTION);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenNotStartedWhenDwellingOfForeignServicePersonnelOrDiplomatsThenNotEligible()
      throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenNotStartedWhenNoPersonInEligibleAgeRangeThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notStarted, CategoryDTO.CategoryName.NO_PERSON_IN_ELIGIBLE_AGE_RANGE);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenNotStartedWhenDeceasedThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.DECEASED);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  // In progress to not eligible
  @Test
  public void givenInProgressWhenVacantOrEmptyThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.VACANT_OR_EMPTY);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenInProgressWhenNonResidentialAddressThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.NON_RESIDENTIAL_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenInProgressWhenAddressOccupiedButNoResidentThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenInProgressWhenCommunalEstablishmentInstitutionThenNotEligible()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.COMMUNAL_ESTABLISHMENT_INSTITUTION);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenInProgressWhenDwellingOfForeignServicePersonnelOrDiplomatsThenNotEligible()
      throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenInProgressWhenNoPersonInEligibleAgeRangeThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            inProgress, CategoryDTO.CategoryName.NO_PERSON_IN_ELIGIBLE_AGE_RANGE);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenInProgressWhenDeceasedThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.DECEASED);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  // Complete to not eligible
  @Test
  public void givenCompleteWhenVacantOrEmptyThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.VACANT_OR_EMPTY);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenCompleteWhenNonResidentialAddressThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.NON_RESIDENTIAL_ADDRESS);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenCompleteWhenAddressOccupiedButNoResidentThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.ADDRESS_OCCUPIED_NO_RESIDENT);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenCompleteWhenCommunalEstablishmentInstitutionThenNotEligible()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.COMMUNAL_ESTABLISHMENT_INSTITUTION);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenCompleteWhenDwellingOfForeignServicePersonnelOrDiplomatsThenNotEligible()
      throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenCompleteWhenNoPersonInEligibleAgeRangeThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            complete, CategoryDTO.CategoryName.NO_PERSON_IN_ELIGIBLE_AGE_RANGE);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  @Test
  public void givenCompleteWhenDeceasedThenNotEligible() throws CTPException {
    // Given
    CaseGroupStatus complete = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(complete, CategoryDTO.CategoryName.DECEASED);

    // Then
    assertEquals(CaseGroupStatus.NOTELIGIBLE, destinationState);
  }

  // Social outcomes to in progress
  @Test
  public void givenRefusalWhenEqLaunchThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus refusal = CaseGroupStatus.REFUSAL;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(refusal, CategoryDTO.CategoryName.EQ_LAUNCH);

    // Then
    assertEquals(CaseGroupStatus.INPROGRESS, destinationState);
  }

  @Test
  public void givenOtherNonResponseWhenEqLaunchThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus otherNonResponse = CaseGroupStatus.OTHERNONRESPONSE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(otherNonResponse, CategoryDTO.CategoryName.EQ_LAUNCH);

    // Then
    assertEquals(CaseGroupStatus.INPROGRESS, destinationState);
  }

  @Test
  public void givenNotEligibleWhenEqLaunchThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus notEligible = CaseGroupStatus.NOTELIGIBLE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notEligible, CategoryDTO.CategoryName.EQ_LAUNCH);

    // Then
    assertEquals(CaseGroupStatus.INPROGRESS, destinationState);
  }

  @Test
  public void givenUnknownEligibilityWhenEqLaunchThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus unknownEligibility = CaseGroupStatus.UNKNOWNELIGIBILITY;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(unknownEligibility, CategoryDTO.CategoryName.EQ_LAUNCH);

    // Then
    assertEquals(CaseGroupStatus.INPROGRESS, destinationState);
  }

  // Social outcomes to complete
  @Test
  public void givenRefusalWhenEqLaunchThenComplete() throws CTPException {
    // Given
    CaseGroupStatus refusal = CaseGroupStatus.REFUSAL;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            refusal, CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);

    // Then
    assertEquals(CaseGroupStatus.COMPLETE, destinationState);
  }

  @Test
  public void givenOtherNonResponseWhenEqLaunchThenComplete() throws CTPException {
    // Given
    CaseGroupStatus otherNonResponse = CaseGroupStatus.OTHERNONRESPONSE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            otherNonResponse, CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);

    // Then
    assertEquals(CaseGroupStatus.COMPLETE, destinationState);
  }

  @Test
  public void givenNotEligibleWhenEqLaunchThenComplete() throws CTPException {
    // Given
    CaseGroupStatus notEligible = CaseGroupStatus.NOTELIGIBLE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            notEligible, CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);

    // Then
    assertEquals(CaseGroupStatus.COMPLETE, destinationState);
  }

  @Test
  public void givenUnknownEligibilityWhenEqLaunchThenComplete() throws CTPException {
    // Given
    CaseGroupStatus unknownEligibility = CaseGroupStatus.UNKNOWNELIGIBILITY;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            unknownEligibility, CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);

    // Then
    assertEquals(CaseGroupStatus.COMPLETE, destinationState);
  }
}
