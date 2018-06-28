package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQueries;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain entity representing the report table */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "report", schema = "casesvc")
@NamedStoredProcedureQueries({
  // CHECKSTYLE IGNORE Indentation FOR NEXT 1 LINES
  @NamedStoredProcedureQuery(
      name = "CaseReport.chasingReport",
      procedureName = "casesvc.generate_response_chasing_report",
      parameters = {@StoredProcedureParameter(mode = ParameterMode.OUT, type = Boolean.class)}),
  // CHECKSTYLE IGNORE Indentation FOR NEXT 1 LINES
  @NamedStoredProcedureQuery(
      name = "CaseReport.caseEventsReport",
      procedureName = "casesvc.generate_case_events_report",
      parameters = {@StoredProcedureParameter(mode = ParameterMode.OUT, type = Boolean.class)})
})
public class CaseReport {
  @Id
  @Column(name = "id")
  private UUID id;

  @Column(name = "reportpk")
  private Integer reportPK;

  @Column(name = "reporttypefk")
  private String reportTypeFK;

  @Column(name = "contents")
  private String contents;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;
}
