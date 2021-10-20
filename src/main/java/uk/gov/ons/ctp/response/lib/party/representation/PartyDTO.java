package uk.gov.ons.ctp.response.lib.party.representation;

import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class PartyDTO {
  private String id;
  private String sampleUnitType;
  private String sampleSummaryId;
  private String sampleUnitRef;
  private String name;
  private Attributes attributes;
  private List<Association> associations;

  private String status;
}
