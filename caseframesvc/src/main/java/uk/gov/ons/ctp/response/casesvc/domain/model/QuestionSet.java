package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

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
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "questionset", schema = "caseframe")
public class QuestionSet implements Serializable {

  private static final long serialVersionUID = -8939812032922335421L;

  @Id
  @Column(name = "questionset")
  private String questionSet;

  private String description;

}
