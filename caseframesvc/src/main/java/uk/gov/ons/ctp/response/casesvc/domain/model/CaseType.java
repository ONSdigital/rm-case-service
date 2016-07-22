package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "casetype", schema = "caseframe")
public class CaseType implements Serializable {

  private static final long serialVersionUID = -2974430124226920391L;

  @Id
  @GeneratedValue
  @Column(name = "casetypeid")
  private Integer caseTypeId;

  private String name;

  private String description;

  @Column(name = "actionplanid")
  private Integer actionPlanId;

  @Column(name = "questionset")
  private String questionSet;

}
