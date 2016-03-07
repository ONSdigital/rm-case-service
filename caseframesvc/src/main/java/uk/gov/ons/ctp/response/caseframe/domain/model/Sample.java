package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.NamedStoredProcedureQuery;
import javax.persistence.ParameterMode;
import javax.persistence.StoredProcedureParameter;
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
@Table(name = "sample", schema = "caseframe")
@NamedStoredProcedureQuery(name = "generate_cases", procedureName = "caseframe.generate_cases", parameters = {
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_sampleid", type = Integer.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_geog_area_type", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.IN, name = "p_geog_area_code", type = String.class),
    @StoredProcedureParameter(mode = ParameterMode.OUT, name = "success", type = Boolean.class) })
public class Sample implements Serializable {

  private static final long serialVersionUID = -7537916260549107271L;

  @Id
  @GeneratedValue
  @Column(name = "sampleid")
  private Integer sampleId;

  private String sampleName;

  private String description;

  private String addressCriteria;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "surveyid")
  private Integer surveyId;

}
