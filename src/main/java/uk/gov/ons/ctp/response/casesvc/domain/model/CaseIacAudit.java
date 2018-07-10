package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.sql.Timestamp;
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

/** Domain model object. */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "caseiacaudit", schema = "casesvc")
public class CaseIacAudit {

  private static final long serialVersionUID = -2971565755952967983L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "caseiacauditseq_gen")
  @GenericGenerator(
      name = "caseiacauditseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @org.hibernate.annotations.Parameter(
            name = "sequence_name",
            value = "casesvc.caseiacauditseq"),
        @org.hibernate.annotations.Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "caseiacauditpk")
  private Integer caseIacAuditPK;

  @Column(name = "casefk")
  private int caseFK;

  @Column(name = "iac")
  private String iac;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;
}
