package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "casegroupstatusaudit", schema = "casesvc")
public class CaseGroupStatusAudit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "casegroupstatusauditseq_gen")
  @GenericGenerator(
      name = "casegroupstatusauditseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @org.hibernate.annotations.Parameter(
            name = "sequence_name",
            value = "casesvc.casegroupstatusauditseq"),
        @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "case_group_status_audit_pk")
  private Integer caseGroupStatusAuditPK;

  @Column(name = "case_group_fk")
  private int caseGroupFK;

  @Column(name = "party_id")
  private UUID partyId;

  @Column(name = "status")
  private CaseGroupStatus status;

  @Column(name = "created_date_time")
  private Timestamp createdDateTime;
}
