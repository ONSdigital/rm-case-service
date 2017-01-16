package uk.gov.ons.ctp.response.casesvc.representation;


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
public class ReportTypeDTO {

  private Integer reportTypeId;
  
  private String reportType;
  
  private Integer orderId;;
  
}
