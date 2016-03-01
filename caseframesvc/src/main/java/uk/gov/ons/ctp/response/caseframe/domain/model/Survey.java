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
@Table(name="survey", schema="caseframe")
public class Survey implements Serializable {

  private static final long serialVersionUID = -256606660399234997L;

  @Id
  @GeneratedValue
  @Column(name="surveyid")
  private Integer surveyid;

  private String surveyName;

  private String description;

}
