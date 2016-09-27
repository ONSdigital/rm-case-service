package uk.gov.ons.ctp.response.action.export;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.config.MongoTemplateLoader;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;

@SpringBootConfiguration
public class GenericTestConfig {

  @Bean
  public MongoTemplateLoader mongoTemplateLoader() {
    return new MongoTemplateLoader();
  }

  @Bean
  public TransformationService transformationService() {
    return new TransformationServiceImpl();
  }
}
