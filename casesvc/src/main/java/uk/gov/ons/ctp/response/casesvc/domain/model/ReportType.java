package uk.gov.ons.ctp.response.casesvc.domain.model;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reporttype", schema = "casesvc")
public class ReportType {

  @Id
  @Column(name = "reporttypeid")
  private Integer reportTypeId;

  @Column(name = "reporttype")
  @Enumerated(EnumType.STRING)
  private ReportDTO.ReportType reportType;

  @Column(name = "orderid")
  private Integer orderId;
  
}
