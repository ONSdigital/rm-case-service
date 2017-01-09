package uk.gov.ons.ctp.response.casesvc.representation;

import java.sql.Date;
import java.sql.Timestamp;

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
    UNIVERSITY;
  }

  private Integer reportId;
  
  private ReportType reportType;

  private Date reportDate;
  
  private String contents;
  
  private Timestamp createddatetime;
  
}
