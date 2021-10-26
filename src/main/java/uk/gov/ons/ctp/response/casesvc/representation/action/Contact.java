package uk.gov.ons.ctp.response.casesvc.representation.action;

import lombok.Data;

@Data
public class Contact {
  private String forename;
  private String surname;
  private String emailAddress;
}
