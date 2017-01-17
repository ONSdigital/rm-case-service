package uk.gov.ons.ctp.response.casesvc.representation;


import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ReportDTO {

  /**
   * enum for ReportType
   */
  public enum ReportType {
    HH_RETURNRATE,
    HH_NORETURNS,
    HH_RETURNRATE_SAMPLE,
    HH_RETURNRATE_LA,
    CE_RETURNRATE_UNI,
    CE_RETURNRATE_SHOUSING,
    CE_RETURNRATE_HOTEL,
    HL_METRICS,
    PRINT_VOLUMES,
    HH_OUTSTANDING_CASES,
    SH_OUTSTANDING_CASES,
    CE_OUTSTANDING_CASES;
  }

  private Integer reportId;
  
  private ReportType reportType;
  
  private String contents;
  
  private Date createdDateTime;
  
}
