package uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@SuperBuilder
public class SampleUnit {

  protected String id;
  @NotNull protected String sampleUnitRef;
  @NotNull protected String sampleUnitType;
  protected String partyId;
  @NotNull protected String collectionInstrumentId;
  protected boolean activeEnrolment;
  protected String actionPlanId;
}
