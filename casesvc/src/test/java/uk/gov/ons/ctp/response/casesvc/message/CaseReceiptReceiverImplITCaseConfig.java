package uk.gov.ons.ctp.response.casesvc.message;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:CaseReceiptReceiverImplITCase-context.xml" })
public class CaseReceiptReceiverImplITCaseConfig {
}
