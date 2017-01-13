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
   * enum for case state
   */
  public enum ReportType {
    UNIVERSITY,
    LA_RETURN_METRICS, 
    SAMPLE_RETURN_METRICS,
    HH_NORETURN_METRICS,
    HH_RETURN_METRICS, 
    CE_RETURN_METRICS_HOTEL,
    CE_RETURN_METRICS_UNI;
  }

  private Integer reportId;
  
  private ReportType reportType;

  private Date reportDate;
  
  private String contents;
  
  private Date createddatetime;
  
}
