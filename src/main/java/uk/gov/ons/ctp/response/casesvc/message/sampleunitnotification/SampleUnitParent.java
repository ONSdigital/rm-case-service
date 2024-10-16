package uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification;

import lombok.*;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
public class SampleUnitParent extends SampleUnit {
  protected String collectionExerciseId;
  protected SampleUnitChildren sampleUnitChildren;
}
