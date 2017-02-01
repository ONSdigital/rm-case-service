package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "casetype", schema = "casesvc")
public class CaseType implements Serializable {

  private static final long serialVersionUID = 1860777024715053996L;

  @Id
  @Column(name = "casetypeid")
  private Integer caseTypeId;

  private String name;

  private String description;

  @Column(name = "respondenttype")
  private String respondentType;
  
  @Column(name = "questionset")
  private String questionSet;

}
