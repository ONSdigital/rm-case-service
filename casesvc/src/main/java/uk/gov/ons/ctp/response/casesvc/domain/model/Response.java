package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @Id
  @GeneratedValue
  @Column(name = "responseid")
  private Integer responseId;
  
  @ManyToOne(fetch=FetchType.LAZY)
  @JoinColumn(name="caseid")
  private Case caze;

  @Column(name = "inboundchannel")
  private String inboundChannel;
  
  @Column(name = "datetime")
  private Timestamp dateTime;
}
