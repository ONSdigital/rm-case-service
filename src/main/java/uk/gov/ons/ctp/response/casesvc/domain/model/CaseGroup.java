package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

/** Domain model object. */
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
  @GenericGenerator(
      name = "casegroupidseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "casesvc.casegroupseq"),
        @Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "case_group_pk")
  private int caseGroupPK;

  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  private UUID id;

  @Column(name = "party_id")
  private UUID partyId;

  @Column(name = "sample_unit_ref")
  private String sampleUnitRef;

  @Column(name = "sample_unit_type")
  private String sampleUnitType;

  @Column(name = "survey_id")
  private UUID surveyId;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private CaseGroupStatus status;

  @Column(name = "status_change_timestamp")
  private Timestamp statusChangeTimestamp;
}
