package uk.gov.ons.ctp.response.action.export.message;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ImportResource;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:springintegration/ActionExportReceiverSITest-context.xml" })
public class ActionExportReceiverSITestConfig {
}
