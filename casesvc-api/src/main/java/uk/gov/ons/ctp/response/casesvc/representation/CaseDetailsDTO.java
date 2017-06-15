package uk.gov.ons.ctp.response.casesvc.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Domain model object to represent the full details of a Case (Case, CaseGroup and CaseEvents)
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseDetailsDTO {
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

    private UUID id;
    private CaseDTO.CaseState state;
    private String iac;

    private UUID actionPlanId;
    private UUID collectionInstrumentId;
    private UUID partyId;

    private String sampleUnitType;

    private String createdBy;
    private Date createdDateTime;

    private List<ResponseDTO> responses;

    private CaseGroupDTO caseGroup;
    private List<CaseEventDTO> caseEvents;
}
