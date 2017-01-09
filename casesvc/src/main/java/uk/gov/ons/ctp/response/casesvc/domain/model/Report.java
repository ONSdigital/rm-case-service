package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.sql.Date;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
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
@Table(name = "reportrepository", schema = "casesvc")
public class Report {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "casecontactseq_gen")
  @SequenceGenerator(name = "casecontactseq_gen", sequenceName = "casesvc.contactidseq")
  @Column(name = "reportid")
  private Integer reportId;

  @Column(name = "reporttype")
  @Enumerated(EnumType.STRING)
  private ReportDTO.ReportType reportType;

  @Column(name = "reportdate")
  private Date reportDate;

  private String contents;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;

}
