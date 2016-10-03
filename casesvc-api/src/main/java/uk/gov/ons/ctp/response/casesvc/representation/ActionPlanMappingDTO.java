package uk.gov.ons.ctp.response.casesvc.representation;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ActionPlanMappingDTO {
  private Integer actionPlanMappingId;
  
  private Integer actionPlanId;

  private Integer caseTypeId;

  private Boolean isDefault;
  
  private String inboundChannel;

  private String variant;

  private String outboundChannel;
}
