package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
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
    ACTIVATED, DEACTIVATED, DISABLED, HOUSEHOLD_PAPER_REQUESTED, INDIVIDUAL_RESPONSE_REQUESTED, REPLACED
  }

  private String actionPlanID;
  private String caseGroupID;
  private String collectionInstrumentID;
  private String createdBy;
  private String id;
  private String partyID;
  private String sampleUnitType;
  private String sampleUnitRef;

  private CaseState state;

  private Date createdDateTime;

  private ResponseDTO[] responses;
}
