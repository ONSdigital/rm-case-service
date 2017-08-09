package uk.gov.ons.ctp.response.casesvc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;
import java.util.UUID;

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
  @GenericGenerator(name = "casegroupidseq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
          parameters = {
            @Parameter(name = "sequence_name", value = "casesvc.casegroupseq"),
            @Parameter(name = "increment_size", value = "1")
  })
  @Column(name = "casegrouppk")
  private int caseGroupPK;

  @Column(name = "collectionexerciseid")
  private UUID collectionExerciseId;

  private UUID id;

  @Column(name = "partyid")
  private UUID partyId;

  @Column(name = "sampleunitref")
  private String sampleUnitRef;

  @Column(name = "sampleunittype")
  private String sampleUnitType;

}
