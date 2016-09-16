package uk.gov.ons.ctp.response.action.export;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.response.action.export.service.FileService;
import uk.gov.ons.ctp.response.action.export.service.impl.FileServiceImpl;

@SpringBootConfiguration
public class GenericTestConfig {
  @Bean
  public FileService kironaDrsService() {
    return new FileServiceImpl();
  }
}
