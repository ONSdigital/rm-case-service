package uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class SampleUnitChildren {
  protected List<SampleUnit> sampleUnitchildren;
}
