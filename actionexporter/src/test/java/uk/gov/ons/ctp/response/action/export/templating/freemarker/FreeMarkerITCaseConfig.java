package uk.gov.ons.ctp.response.action.export.templating.freemarker;

import com.mongodb.Mongo;
import freemarker.template.TemplateExceptionHandler;
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

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;

@PropertySource("classpath:application-test.properties")
@EnableMongoRepositories(basePackages = "uk.gov.ons.ctp.response.action.export.templating.freemarker.repository")
@SpringBootConfiguration
public class FreeMarkerITCaseConfig {

  @Value("${mongodb.server}")
  private String databseServerName;

  @Value("${mongodb.database}")
  private String databaseName;

  public @Bean
  Mongo mongo() throws Exception {
    return new Mongo(databseServerName);
  }

  public @Bean MongoTemplate mongoTemplate() throws Exception {
    return new MongoTemplate(mongo(), databaseName);
  }

  @Bean
  public MongoTemplateLoader mongoTemplateLoader() {
    return new MongoTemplateLoader();
  }

  @Bean
  public freemarker.template.Configuration configuration() {
    freemarker.template.Configuration configuration = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
    configuration.setTemplateLoader(mongoTemplateLoader());
    configuration.setDefaultEncoding(UTF8.name());
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setLogTemplateExceptions(false);
    return configuration;
  }

  @Bean
  public FreeMarkerService freeMarkerService() {
    return new FreeMarkerServiceImpl();
  }

  @Bean
  public TransformationService transformationService() {
    return new TransformationServiceImpl();
  }
}
