package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

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
 * Domain model object
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="action", schema="caseframe")
public class Action implements Serializable {

  private static final long serialVersionUID = -717740149545273786L;

  @Id
  @GeneratedValue
  @Column(name="actionid")
  private Integer actionId;

  @Column(name="caseid")
  private Integer caseId;

  @Column(name="actionplanid")
  private Integer actionPlanId;

  private String actionStatus;

  private String actionType;

  private String priority;

  private String situation;

  private Timestamp createdDatetime;

  private String createdBy;

}
