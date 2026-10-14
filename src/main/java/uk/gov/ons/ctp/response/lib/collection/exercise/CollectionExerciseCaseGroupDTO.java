package uk.gov.ons.ctp.response.lib.collection.exercise;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CollectionExerciseCaseGroupDTO {

  private CollectionExerciseDTO collectionExercise;
  private String caseGroupStatus;
}
