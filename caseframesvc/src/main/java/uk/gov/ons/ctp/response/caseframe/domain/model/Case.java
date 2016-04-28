package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;

/**
 * Domain model object.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "case", schema = "caseframe")
public class Case implements Serializable {

  private static final long serialVersionUID = -3769020357396562359L;

  @Id
  @GeneratedValue
  @Column(name = "caseid")
  private Integer caseId;

  private Integer uprn;

  @Enumerated(EnumType.STRING)
  private CaseDTO.CaseState status;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "createddatetime")
  private Timestamp createdDatetime;

  @Column(name="createdby")
  private String createdBy;

  @Column(name = "sampleid")
  private Integer sampleId;

  @Column(name = "actionplanid")
  private Integer actionPlanId;

  @Column(name = "surveyid")
  private Integer surveyId;

  @Column(name = "questionset")
  private String questionSet;

}
