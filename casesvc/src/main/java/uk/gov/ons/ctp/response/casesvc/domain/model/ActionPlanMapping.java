package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
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
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "actionplanmapping", schema = "casesvc")
public class ActionPlanMapping implements Serializable {
  @Id
  @GeneratedValue
  @Column(name = "actionplanmappingid")
  private Integer actionPlanMappingId;
  
  @Column(name = "actionplanid")
  private Integer actionPlanId;

  @Column(name = "casetypeid")
  private Integer caseTypeId;

  @Column(name = "casegroupid")
  private Integer caseGroupId;

  @Column(name = "isdefault")
  private Boolean isDefault;
  
  @Column(name = "inboundchannel")
  private String inboundChannel;

  private String variant;

  @Column(name = "outboundchannel")
  private String outboundChannel;
}
