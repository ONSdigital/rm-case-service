package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;
import java.util.UUID;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
    ACCOUNT_CREATED, ACTIVATED, DEACTIVATED, DISABLED, HOUSEHOLD_PAPER_REQUESTED, INDIVIDUAL_RESPONSE_REQUESTED, REPLACED, CASE_CREATED
  }

  private UUID id;
  private CaseState state;

  private UUID actionPlanId;
  private UUID collectionInstrumentId;
  private UUID partyId;
  
  private String sampleUnitType;

  private String createdBy;
  private Date createdDateTime;

  private ResponseDTO[] responses;
}
