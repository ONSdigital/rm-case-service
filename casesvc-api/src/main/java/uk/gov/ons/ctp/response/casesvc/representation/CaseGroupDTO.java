package uk.gov.ons.ctp.response.casesvc.representation;


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
public class CaseGroupDTO {
  private String collectionExerciseID;
  private String id;
  private String partyID;
  private String sampleUnitRef;
  private String sampleUnitType;
}
