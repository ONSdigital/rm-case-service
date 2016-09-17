package uk.gov.ons.ctp.response.action.export.templating;

import com.mongodb.Mongo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.config.MongoTemplateLoader;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.service.FreeMarkerService;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.service.impl.FreeMarkerServiceImpl;

@PropertySource("classpath:application-test.properties")
@EnableMongoRepositories(basePackages = "uk.gov.ons.ctp.response.action.export.templating.freemarker.repository")
@SpringBootConfiguration
public class FreeMarkerITCaseConfig {

  @Value("${mongodb.server}")
  private String serverName;

  @Value("${mongodb.database}")
  private String databaseName;

  public @Bean
  Mongo mongo() throws Exception {
    return new Mongo(serverName);
  }

  public @Bean MongoTemplate mongoTemplate() throws Exception {
    return new MongoTemplate(mongo(), databaseName);
  }

  @Bean
  public MongoTemplateLoader mongoTemplateLoader() {
    return new MongoTemplateLoader();
  }

  @Bean
  public TransformationService transformationService() {
    return new TransformationServiceImpl();
  }

  @Bean
  public FreeMarkerService freeMarkerService() {
    return new FreeMarkerServiceImpl();
  }
}
