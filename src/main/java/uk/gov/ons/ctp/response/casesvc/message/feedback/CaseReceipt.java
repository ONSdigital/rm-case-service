package uk.gov.ons.ctp.response.casesvc.message.feedback;

import javax.xml.datatype.XMLGregorianCalendar;

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
    private XMLGregorianCalendar responseDateTime;
    private String partyId;
}
