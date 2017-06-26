package uk.gov.ons.ctp.response.casesvc.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

/**
 * Domain model object to represent a Case
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseDTO {
  /**
   * enum for case state
   */
  public enum CaseState {
    ACTIONABLE, INACTIONABLE, REPLACEMENT_INIT, SAMPLED_INIT;
  }

  /**
   * enum for Case event
   */
  public enum CaseEvent {
    ACCOUNT_CREATED, ACTIVATED, DEACTIVATED, DISABLED, HOUSEHOLD_PAPER_REQUESTED, INDIVIDUAL_RESPONSE_REQUESTED,
    REPLACED, CASE_CREATED
  }

  private UUID id;
  private CaseState state;

  private UUID actionPlanId;
  private UUID collectionInstrumentId;
  private UUID partyId;

  private String caseRef;
  private String createdBy;
  private String sampleUnitType;

  private Date createdDateTime;

  private ResponseDTO[] responses;
}
