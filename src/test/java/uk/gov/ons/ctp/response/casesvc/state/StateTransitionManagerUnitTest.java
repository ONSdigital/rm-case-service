package uk.gov.ons.ctp.response.casesvc.state;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.lib.common.error.CTPException;
import uk.gov.ons.ctp.response.lib.common.state.StateTransitionManager;

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
    assertEquals(CaseState.ACTIONABLE, destinationState);
  }

  @Test
  public void givenCaseStateReplacementInitWhenReplacedThenActionable() throws CTPException {
    // Given
    CaseState replacementInit = CaseState.REPLACEMENT_INIT;

    // When
    CaseState destinationState =
        caseStateMachine.transition(replacementInit, CaseDTO.CaseEvent.REPLACED);

    // Then
    assertEquals(CaseState.ACTIONABLE, destinationState);
  }

  @Test
  public void givenCaseStateActionableWhenDisabledThenInactionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(actionable, CaseDTO.CaseEvent.DISABLED);

    // Then
    assertEquals(CaseState.INACTIONABLE, destinationState);
  }

  @Test
  public void givenCaseStateActionableWhenDeactivatedThenInactionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(actionable, CaseDTO.CaseEvent.DEACTIVATED);

    // Then
    assertEquals(CaseState.INACTIONABLE, destinationState);
  }

  @Test
  public void givenCaseStateInactionableWhenDisabledThenInactionable() throws CTPException {
    // Given
    CaseState inactionable = CaseState.INACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(inactionable, CaseDTO.CaseEvent.DISABLED);

    // Then
    assertEquals(CaseState.INACTIONABLE, destinationState);
  }

  @Test
  public void givenCaseStateInactionableWhenDeactivatedThenInactionable() throws CTPException {
    // Given
    CaseState inactionable = CaseState.INACTIONABLE;

    // When
    CaseState destinationState =
        caseStateMachine.transition(inactionable, CaseDTO.CaseEvent.DEACTIVATED);

    // Then
    assertEquals(CaseState.INACTIONABLE, destinationState);
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
    assertEquals(CaseGroupStatus.INPROGRESS, destinationState);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenEQLaunchThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.EQ_LAUNCH);

    // Then
    assertEquals(CaseGroupStatus.INPROGRESS, destinationState);
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
    assertEquals(CaseGroupStatus.COMPLETE, destinationState);
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
    assertEquals(CaseGroupStatus.COMPLETEDBYPHONE, destinationState);
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
    assertEquals(CaseGroupStatus.NOLONGERREQUIRED, destinationState);
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
    assertEquals(CaseGroupStatus.COMPLETE, destinationState);
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
    assertEquals(CaseGroupStatus.COMPLETEDBYPHONE, destinationState);
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
    assertEquals(CaseGroupStatus.NOLONGERREQUIRED, destinationState);
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
    assertEquals(CaseGroupStatus.COMPLETEDBYPHONE, destinationState);
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
    assertEquals(CaseGroupStatus.NOLONGERREQUIRED, destinationState);
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

  @Test
  public void testCaseGroupTransitionFromCompleteToNotStarted() throws CTPException {
    // Given
    CaseGroupStatus caseGroupStatus = CaseGroupStatus.COMPLETE;

    // When
    CaseGroupStatus destinationState =
        caseGroupStateMachine.transition(
            caseGroupStatus, CategoryDTO.CategoryName.COMPLETED_TO_NOTSTARTED);

    // Then
    assertEquals(CaseGroupStatus.NOTSTARTED, destinationState);
  }
}
