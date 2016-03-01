package uk.gov.ons.ctp.response.caseframe.domain.model;

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
 * Domain model object
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "actionplan", schema = "caseframe")
public class ActionPlan implements Serializable {

  private static final long serialVersionUID = -3699270047327231721L;

  @Id
  @GeneratedValue
  @Column(name = "actionplanid")
  private Integer actionPlanId;

  @Column(name = "actionplan_name")
  private String actionPlanName;

  private String description;

  private String rules;

}
