package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.util.UUID;
import javax.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_action", schema = "casesvc")
public class CaseAction {

  @Id
  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  @Column(name = "case_id")
  private UUID caseId;

  @Column(name = "party_id")
  private UUID partyId;

  @Column(name = "sample_unit_ref")
  private String sampleUnitRef;

  @Column(name = "sample_unit_type")
  private String sampleUnitType;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  private CaseGroupStatus status;

  @Column(name = "survey_id")
  private UUID surveyId;

  @Column(name = "sample_unit_id")
  private UUID sampleUnitId;

  @Column(name = "collection_instrument_id")
  private UUID collectionInstrumentId;

  @Column(name = "iac")
  private String iac;

  @Column(name = "active_enrolment")
  private boolean activeEnrolment;
}
