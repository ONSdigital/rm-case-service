package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object to represent a Case */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseDTO {

  /** enum for Case event */
  public enum CaseEvent {
    ACCOUNT_CREATED,
    ACTIONPLAN_CHANGED,
    ACTIVATED,
    DEACTIVATED,
    DISABLED,
    HOUSEHOLD_PAPER_REQUESTED,
    INDIVIDUAL_RESPONSE_REQUESTED,
    REPLACED,
    CASE_CREATED
  }

  private CaseState state;

  private UUID id;
  private UUID sampleUnitId;
  private UUID actionPlanId;
  private UUID collectionInstrumentId;
  private UUID partyId;

  private String caseRef;
  private String createdBy;
  private String sampleUnitType;

  private Date createdDateTime;

  private ResponseDTO[] responses;
}
