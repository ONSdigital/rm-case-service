package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object.
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "questionnaire", schema = "caseframe")
public class Questionnaire implements Serializable {

  private static final long serialVersionUID = -2070377259761460200L;

  @Id
  @Column(name = "questionnaireid")
  private Integer questionnaireId;

  private String iac;

  @Column(name = "caseid")
  private Integer caseId;

  private String state;

  @Column(name = "dispatchdatetime")
  private Timestamp dispatchDateTime;

  @Column(name = "responsedatetime")
  private Timestamp responseDateTime;

  @Column(name = "receiptdatetime")
  private Timestamp receiptDateTime;

  @Column(name = "questionset")
  private String questionSet;

}
