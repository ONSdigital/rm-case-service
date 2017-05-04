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

  private Integer caseGroupId;

  private String sampleUnitType;

  private String sampleUnitRef;

  private String partyId;
  
  private String collectionExerciseId;
}
