package uk.gov.ons.ctp.response.action.export;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;

@SpringBootConfiguration
public class GenericTestConfig {
  @Bean
  public TransformationService kironaDrsService() {
    return new TransformationServiceImpl();
  }
}
