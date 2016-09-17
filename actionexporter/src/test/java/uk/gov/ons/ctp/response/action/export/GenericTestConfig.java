package uk.gov.ons.ctp.response.action.export;

import com.mongodb.Mongo;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.config.MongoTemplateLoader;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;

@SpringBootConfiguration
@EnableMongoRepositories(basePackages = "uk.gov.ons.ctp.response.action.export.templating.freemarker.repository")
public class GenericTestConfig {

  public @Bean
  Mongo mongo() throws Exception {
    return new Mongo("localhost");
  }

  public @Bean MongoTemplate mongoTemplate() throws Exception {
    return new MongoTemplate(mongo(), "actionExport");
  }

  @Bean
  public MongoTemplateLoader mongoTemplateLoader() {
    return new MongoTemplateLoader();
  }

  @Bean
  public TransformationService transformationService() {
    return new TransformationServiceImpl();
  }
}
