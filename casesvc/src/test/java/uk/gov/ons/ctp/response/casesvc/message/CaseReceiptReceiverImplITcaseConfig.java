package uk.gov.ons.ctp.response.casesvc.message;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

@SpringBootConfiguration
@ImportResource(locations = { "classpath:springintegration/CaseReceiptReceiverImplITCase-context.xml" })
public class CaseReceiptReceiverImplITcaseConfig {
  @Bean
  public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
    return new PropertySourcesPlaceholderConfigurer();
  }
}
