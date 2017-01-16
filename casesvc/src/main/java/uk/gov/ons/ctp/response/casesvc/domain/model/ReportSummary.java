package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReportSummary {

  private Integer reportId;
  
  private ReportDTO.ReportType reportType;
  
  private Date createdDateTime;

}
