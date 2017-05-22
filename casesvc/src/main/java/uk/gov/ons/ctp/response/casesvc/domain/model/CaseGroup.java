package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

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

  private static final long serialVersionUID = -2971565755952967983L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "casegroupidseq_gen")
  @GenericGenerator(name = "casegroupidseq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
      @Parameter(name = "sequence_name", value = "casesvc.casegroupidseq"),
      @Parameter(name = "increment_size", value = "1")
  })

  @Column(name = "casegroupPK")
  private int caseGroupPK;
  
  @Column(name = "collectionexerciseid")
  private String collectionExerciseId;

  private String id;
  
  @Column(name = "partyid")
  private String partyId;

  @Column(name = "sampleunitref")
  private String sampleUnitRef;

  @Column(name = "sampleunittype")
  private String sampleUnitType;




}
