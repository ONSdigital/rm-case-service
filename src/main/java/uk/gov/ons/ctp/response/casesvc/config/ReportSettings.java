package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;

/** Config POJO for Reports */
@CoverageIgnore
@Data
public class ReportSettings {
  private boolean cronEnabled;
  private String cronExpression;
}
