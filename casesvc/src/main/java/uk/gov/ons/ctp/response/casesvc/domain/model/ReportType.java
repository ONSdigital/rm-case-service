package uk.gov.ons.ctp.response.casesvc.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
  @Column(name = "reporttype")
  private String reportType;

}
