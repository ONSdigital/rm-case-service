package uk.gov.ons.ctp.response.casesvc.state;


import org.junit.Test;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.state.StateTransitionManager;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

import static org.junit.Assert.assertEquals;

public class StateTransitionManagerUnitTest {

  private final CaseSvcStateTransitionManagerFactory factory = new CaseSvcStateTransitionManagerFactory();
  @SuppressWarnings("unchecked")
  private final StateTransitionManager<CaseState, CaseDTO.CaseEvent> caseStateMachine =
          (StateTransitionManager<CaseState, CaseDTO.CaseEvent>) factory.getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_ENTITY);
  @SuppressWarnings("unchecked")
  private final StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName> caseGroupStateMachine =
          (StateTransitionManager<CaseGroupStatus, CategoryDTO.CategoryName>) factory.getStateTransitionManager(CaseSvcStateTransitionManagerFactory.CASE_GROUP);

  @Test
  public void givenCaseStateSampledInitWhenActivatedThenActionable() throws CTPException {
    // Given
    CaseState sampledInit = CaseState.SAMPLED_INIT;

    // When
    CaseState destinationState = caseStateMachine.transition(sampledInit, CaseDTO.CaseEvent.ACTIVATED);

    // Then
    assertEquals(destinationState, CaseState.ACTIONABLE);
  }

  @Test
  public void givenCaseStateReplacementInitWhenReplacedThenActionable() throws CTPException {
    // Given
    CaseState replacementInit = CaseState.REPLACEMENT_INIT;

    // When
    CaseState destinationState = caseStateMachine.transition(replacementInit, CaseDTO.CaseEvent.REPLACED);

    // Then
    assertEquals(destinationState, CaseState.ACTIONABLE);
  }

  @Test
  public void givenCaseStateActionableWhenDisabledThenInactionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState = caseStateMachine.transition(actionable, CaseDTO.CaseEvent.DISABLED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseStateActionableWhenDeactivatedThenInactionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState = caseStateMachine.transition(actionable, CaseDTO.CaseEvent.DEACTIVATED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }


  @Test
  public void givenCaseStateActionableWhenAccountCreatedThenActionable() throws CTPException {
    // Given
    CaseState actionable = CaseState.ACTIONABLE;

    // When
    CaseState destinationState = caseStateMachine.transition(actionable, CaseDTO.CaseEvent.ACCOUNT_CREATED);

    // Then
    assertEquals(destinationState, CaseState.ACTIONABLE);
  }

  @Test
  public void givenCaseStateInactionableWhenDisabledThenInactionable() throws CTPException {
    // Given
    CaseState inactionable = CaseState.INACTIONABLE;

    // When
    CaseState destinationState = caseStateMachine.transition(inactionable, CaseDTO.CaseEvent.DISABLED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseStateInactionableWhenDeactivatedThenInactionable() throws CTPException {
    // Given
    CaseState inactionable = CaseState.INACTIONABLE;

    // When
    CaseState destinationState = caseStateMachine.transition(inactionable, CaseDTO.CaseEvent.DEACTIVATED);

    // Then
    assertEquals(destinationState, CaseState.INACTIONABLE);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenCIDownloadedThenInProgress() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.COLLECTION_INSTRUMENT_DOWNLOADED);

    // Then
    assertEquals(destinationState, CaseGroupStatus.INPROGRESS);
  }

    @Test
    public void givenCaseGroupStateNotStartedWhenEQLaunchThenInProgress() throws CTPException {
      // Given
      CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

      // When
      CaseGroupStatus destinationState = caseGroupStateMachine.transition(notStarted,
              CategoryDTO.CategoryName.EQ_LAUNCH);

      // Then
      assertEquals(destinationState, CaseGroupStatus.INPROGRESS);
    }

  @Test
  public void givenCaseGroupStateNotStartedWhenSuccessfulResponseUploadedThenComplete() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETE);
  }

  @Test
  public void givenCaseGroupStateNotStartedWhenCompletedByPhoneThenCompletedByPhone() throws CTPException {
    // Given
    CaseGroupStatus notStarted = CaseGroupStatus.NOTSTARTED;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(notStarted, CategoryDTO.CategoryName.COMPLETED_BY_PHONE);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETEDBYPHONE);
  }

  @Test
  public void givenCaseGroupStateInProgressWhenSuccessfulResponseUploadThenComplete() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.SUCCESSFUL_RESPONSE_UPLOAD);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETE);
  }

  @Test
  public void givenCaseGroupStateInProgressWhenCompletedByPhoneThenCompletedByPhone() throws CTPException {
    // Given
    CaseGroupStatus inProgress = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(inProgress, CategoryDTO.CategoryName.COMPLETED_BY_PHONE);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETEDBYPHONE);
  }

  @Test
  public void givenCaseGroupStateReopenedWhenCompletedByPhoneThenCompletedByPhone() throws CTPException {
    // Given
    CaseGroupStatus reopened = CaseGroupStatus.REOPENED;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(reopened, CategoryDTO.CategoryName.COMPLETED_BY_PHONE);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETEDBYPHONE);
  }
  
  @Test
  public void givenCaseGroupStateInProgressWhenOfflineResponseProcessedThenComplete() throws CTPException {
    // Given
    CaseGroupStatus reopened = CaseGroupStatus.INPROGRESS;

    // When
    CaseGroupStatus destinationState = caseGroupStateMachine.transition(reopened, CategoryDTO.CategoryName.OFFLINE_RESPONSE_PROCESSED);

    // Then
    assertEquals(destinationState, CaseGroupStatus.COMPLETE);
  }

}
