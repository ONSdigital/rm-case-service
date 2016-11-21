package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "samplecasetypeselector", schema = "casesvc")
public class SampleCaseType implements Serializable {

  private static final long serialVersionUID = -7109840000867720238L;

  @Id
  @Column(name = "samplecasetypeselectorid")
  private Integer sampleCaseTypeId;

  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="sampleid")
  private Sample sample;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "respondenttype")
  private String respondentType;

  @Column(name = "isdefault")
  private Boolean isDefault;
}
