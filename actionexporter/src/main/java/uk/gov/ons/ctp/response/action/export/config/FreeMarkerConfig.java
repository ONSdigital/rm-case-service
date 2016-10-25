package uk.gov.ons.ctp.response.action.export.config;

import freemarker.template.TemplateExceptionHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.config.MongoTemplateLoader;

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;

/**
 * Configuration specific to FreeMarker templating
 */
@Configuration
public class FreeMarkerConfig {

  @Value("${freemarker.delayfornewtemplates}")
  private long delayForNewTemplates;
  /**
   * The bean to store FreeMarker templates in MongoDB
   * @return the loader to store FreeMarker templates in MongoDB
   */
  @Bean
  public MongoTemplateLoader mongoTemplateLoader() {
    return new MongoTemplateLoader();
  }

  /**
   * The FreeMarker configuration
   * @return the FreeMarker configuration
   */
  @Bean
  public freemarker.template.Configuration configuration() {
    freemarker.template.Configuration configuration = new freemarker.template.Configuration(
            freemarker.template.Configuration.VERSION_2_3_25);
    configuration.setTemplateLoader(mongoTemplateLoader());
    configuration.setDefaultEncoding(UTF8.name());
    configuration.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    configuration.setLogTemplateExceptions(false);
    configuration.setTemplateUpdateDelayMilliseconds(delayForNewTemplates);
    return configuration;
  }
}
