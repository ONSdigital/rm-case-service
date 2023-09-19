package uk.gov.ons.ctp.response.casesvc.message.feedback;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CaseReceipt {

  private String caseRef;
  private String caseId;
  private InboundChannel inboundChannel;
  private String partyId;

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String sdsDatasetId;
}
