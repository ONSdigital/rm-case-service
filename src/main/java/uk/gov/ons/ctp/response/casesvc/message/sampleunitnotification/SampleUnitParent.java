package uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
public class SampleUnitParent extends SampleUnit {
  protected String collectionExerciseId;
  protected SampleUnitChildren sampleUnitChildren;
}
