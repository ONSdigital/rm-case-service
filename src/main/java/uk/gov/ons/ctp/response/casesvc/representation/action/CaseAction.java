package uk.gov.ons.ctp.response.casesvc.representation.action;

import java.util.UUID;
import lombok.*;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Builder
public class CaseAction {
  private UUID collectionExerciseId;
  private UUID caseId;
  private UUID partyId;
  private String sampleUnitRef;
  private String sampleUnitType;
  private CaseGroupStatus status;
  private UUID surveyId;
  private UUID sampleUnitId;
  private UUID collectionInstrumentId;
  private String iac;
  private boolean activeEnrolment;
}
