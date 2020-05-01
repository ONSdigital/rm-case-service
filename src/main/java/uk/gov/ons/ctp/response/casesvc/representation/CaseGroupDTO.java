package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseGroupDTO {
  private UUID collectionExerciseId;
  private UUID id;
  private UUID partyId;
  private String sampleUnitRef;
  private String sampleUnitType;
  private CaseGroupStatus caseGroupStatus;
  private UUID surveyId;
}
