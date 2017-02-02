package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
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
@Table(name = "sample", schema = "casesvc")
public class Sample implements Serializable {


  private static final long serialVersionUID = -5990905483071750964L;

  @Id
  @Column(name = "sampleid")
  private Integer sampleId;

  private String name;

  private String description;

  @Column(name = "addresscriteria")
  private String addressCriteria;

  private String survey;
  
  @OneToMany(mappedBy="sample", fetch=FetchType.EAGER)
  private List<SampleCaseType> sampleCaseTypes;

}
