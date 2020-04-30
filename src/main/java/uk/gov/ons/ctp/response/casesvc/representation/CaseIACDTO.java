package uk.gov.ons.ctp.response.casesvc.representation;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CaseIACDTO {
  private String iac;

  public CaseIACDTO() {}
}
