package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;

/** Domain model object to represent reporting Unit case DTO */
@AllArgsConstructor
@Data
public class ReportingUnitCaseDTO {

  private UUID collectionExerciseId;
  private CaseGroupStatus caseGroupStatus;
  private UUID caseId;
  private Date createdDateTime;
  private String iac;
}
