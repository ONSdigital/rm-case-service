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
@Table(name = "casegroup", schema = "casesvc")
public class CaseGroup implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "casegroupid")
  private Integer caseGroupId;
  
  @Column(name = "sampleid")
  private Integer sampleId;

  private Integer uprn;
}
