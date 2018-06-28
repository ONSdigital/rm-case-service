package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;

/** Config POJO for action plan exec params */
@Data
@CoverageIgnore
public class DataGrid {
  private String address;
  private String password;
  private Integer listTimeToLiveSeconds;
  private Integer listTimeToWaitSeconds;
  private Integer reportLockTimeToLiveSeconds;
}
