package uk.gov.ons.ctp.response.casesvc.representation;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CaseGroupDTO {
  private String collectionExerciseID;
  private String id;
  private String partyID;
  private String sampleUnitType;
  private String sampleUnitRef;
}
