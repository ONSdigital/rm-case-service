package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name="caseevent", schema="caseframe")
public class CaseEvent implements Serializable {

  private static final long serialVersionUID = 6034836141646834386L;

  @Id
  @GeneratedValue
  @Column(name="caseeventid")
  private Integer caseEventId;

  @Column(name="caseid")
  private Integer caseId;

  private String description;

  private String createdBy;

  private Timestamp createdDatetime;

  private String category;

}
