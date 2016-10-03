package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
@Table(name = "sample", schema = "casesvc")
public class Sample implements Serializable {

  private static final long serialVersionUID = -7537916260549107271L;

  @Id
  @GeneratedValue
  @Column(name = "sampleid")
  private Integer sampleId;

  private String name;

  private String description;

  @Column(name = "addresscriteria")
  private String addressCriteria;

  private String survey;

}
