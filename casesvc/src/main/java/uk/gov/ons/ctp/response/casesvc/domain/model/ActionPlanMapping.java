package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.message.feedback.InboundChannel;

/**
 * Domain model object.
 */
@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "actionplanmapping", schema = "casesvc")
public class ActionPlanMapping implements Serializable {
  private static final long serialVersionUID = -2115603335146371158L;

  @Id
  @GeneratedValue
  @Column(name = "actionplanmappingid")
  private Integer actionPlanMappingId;

  @Column(name = "actionplanid")
  private Integer actionPlanId;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "isdefault")
  private Boolean isDefault;

  @Column(name = "inboundchannel")
  @Enumerated(EnumType.STRING)
  private InboundChannel inboundChannel;

  private String variant;

  @Column(name = "outboundchannel")
  @Enumerated(EnumType.STRING)
  private OutboundChannel outboundChannel;
}
