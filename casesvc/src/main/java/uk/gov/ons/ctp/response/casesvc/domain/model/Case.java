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
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.PostLoad;
import javax.persistence.Table;

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
  @GeneratedValue
  @Column(name = "caseid")
  private Integer caseId;
  
  @Column(name = "casegroupid")
  private Integer caseGroupId;

  @Column(name = "caseref")
  private String caseRef;
  
  @Enumerated(EnumType.STRING)
  private CaseDTO.CaseState state;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "actionplanmappingid")
  private Integer actionPlanMappingId;

  @OneToOne(fetch=FetchType.EAGER)
  @JoinColumn(name="contactid")
  private Contact contact;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;

  @Column(name = "createdby")
  private String createdBy;

  @OneToMany(mappedBy="caze", fetch=FetchType.EAGER, cascade=CascadeType.ALL)
  private List<Response> responses;

  private String iac;

  @PostLoad
  public void trimIACAfterLoad() {
    if (iac != null) {
      iac = iac.trim();
    }
  }
  

}
