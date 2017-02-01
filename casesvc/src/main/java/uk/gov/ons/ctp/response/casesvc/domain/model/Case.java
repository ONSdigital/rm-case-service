package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Version;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case", schema = "casesvc")
public class Case implements Serializable {

  private static final long serialVersionUID = 7778360895016862176L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "caseidseq_gen")
  @GenericGenerator(name = "caseidseq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
      @Parameter(name = "sequence_name", value = "casesvc.caseidseq"),
      @Parameter(name = "increment_size", value = "1")
  })
  @Column(name = "caseid")
  private Integer caseId;

  @Version
  @Column(name = "optlockversion")
  private int optLockVersion;

  @Column(name = "casegroupid")
  private Integer caseGroupId;

  @Column(name = "sourcecaseid")
  private Integer sourceCaseId;

  @Generated(GenerationTime.INSERT)
  @Column(name = "caseref", nullable = false, unique = true, insertable = false, updatable = false, columnDefinition = "VARCHAR DEFAULT nextval('casesvc.caserefseq')")
  private String caseRef;

  @Enumerated(EnumType.STRING)
  private CaseDTO.CaseState state;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "actionplanmappingid")
  private Integer actionPlanMappingId;

  @OneToOne(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  @JoinColumn(name = "contactid")
  private Contact contact;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;

  @Column(name = "createdby")
  private String createdBy;

  @OneToMany(mappedBy = "caseId", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
  private List<Response> responses;

  private String iac;

  @PostLoad
  public void trimIACAfterLoad() {
    if (iac != null) {
      iac = iac.trim();
    }
  }

}
