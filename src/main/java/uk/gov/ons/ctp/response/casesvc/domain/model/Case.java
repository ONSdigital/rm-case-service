package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import uk.gov.ons.ctp.response.casesvc.representation.CaseState;
import uk.gov.ons.ctp.response.lib.sample.SampleUnitDTO;

/** Domain model object. */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case", schema = "casesvc")
public class Case implements Serializable {
  private static final long serialVersionUID = 7778360895016862176L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "caseseq_gen")
  @GenericGenerator(
      name = "caseseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "casesvc.caseseq"),
        @Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "case_pk")
  private Integer casePK;

  @Column(name = "id")
  private UUID id;

  @Column(name = "sample_unit_id")
  private UUID sampleUnitId;

  @Version
  @Column(name = "optlockversion")
  private int optLockVersion;

  @Column(name = "case_group_fk")
  private Integer caseGroupFK;

  @Column(name = "casegroupid")
  private UUID caseGroupId;

  @Column(name = "source_case_id")
  private Integer sourceCaseId;

  @Generated(GenerationTime.INSERT)
  @Column(name = "case_ref")
  private String caseRef;

  @Enumerated(EnumType.STRING)
  @Column(name = "state_fk")
  private CaseState state;

  @Enumerated(EnumType.STRING)
  @Column(name = "sample_unit_type")
  private SampleUnitDTO.SampleUnitType sampleUnitType;

  @Column(name = "party_id")
  private UUID partyId;

  @Column(name = "collection_instrument_id")
  private UUID collectionInstrumentId;

  @Column(name = "actionplanid", nullable= true)
  private UUID actionPlanId;

  @Column(name = "active_enrolment")
  private boolean activeEnrolment;

  @Column(name = "created_date_time")
  private Timestamp createdDateTime;

  @Column(name = "created_by")
  private String createdBy;

  @OneToMany(mappedBy = "case_fk", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Response> responses;

  @OneToMany(mappedBy = "case_fk", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<CaseIacAudit> iacAudits;

  @Transient private String iac;

  /** Trims spaces from IAC after load */
  @PostLoad
  public void trimIACAfterLoad() {
    if (iac != null) {
      iac = iac.trim();
    }
  }
}
