package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
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
@Table(name = "response", schema = "casesvc")
public class Response implements Serializable {

  private static final long serialVersionUID = 6584615161503944025L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "responseseq_gen")
  @GenericGenerator(name = "responseseq_gen", strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator", parameters = {
      @Parameter(name = "sequence_name", value = "casesvc.responseidseq"),
      @Parameter(name = "increment_size", value = "1")
  })
  @Column(name = "responseid")
  private Integer responseId;

  @Column(name = "caseid")
  private Integer caseId;

  @Column(name = "inboundchannel")
  @Enumerated(EnumType.STRING)
  private InboundChannel inboundChannel;

  @Column(name = "datetime")
  private Timestamp dateTime;
}
