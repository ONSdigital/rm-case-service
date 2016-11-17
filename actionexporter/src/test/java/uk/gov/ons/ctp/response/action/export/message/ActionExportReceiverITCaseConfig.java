package uk.gov.ons.ctp.response.action.export.message;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:springintegration/ActionExportReceiverITCase-context.xml" })
public class ActionExportReceiverITCaseConfig {
}
