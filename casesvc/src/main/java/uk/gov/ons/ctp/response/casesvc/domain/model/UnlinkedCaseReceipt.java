package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import uk.gov.ons.ctp.response.casesvc.representation.InboundChannel;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "unlinkedcasereceipt", schema = "casesvc")
public class UnlinkedCaseReceipt {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "unlinkedcasereceiptidseq_gen")
  @GenericGenerator(name = "unlinkedcasereceiptidseq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
      @Parameter(name = "sequence_name", value = "casesvc.unlinkedcasereceiptidseq"),
      @Parameter(name = "increment_size", value = "1")
  })
  private Integer id;

  @Column(name = "caseref")
  private String caseRef;

  @Column(name = "inboundchannel")
  @Enumerated(EnumType.STRING)
  private InboundChannel inboundChannel;

  @Column(name = "responsedatetime")
  private Timestamp responseDateTime;
}
