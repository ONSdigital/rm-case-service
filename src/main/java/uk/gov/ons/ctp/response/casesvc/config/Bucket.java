package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;

@Data
public class Bucket {
  private String name;
  private String prefix;
}
