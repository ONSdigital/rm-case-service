package uk.gov.ons.ctp.response.action.export.config;

import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;

import java.io.File;

@Configuration
@Slf4j
public class FreeMarkerConfiguration {

  @Autowired
  private ResourceLoader resourceLoader;

  @Bean
  public freemarker.template.Configuration configuration() throws Exception{
    freemarker.template.Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
    File templateDirectory = resourceLoader.getResource("classpath:templates").getFile();
    cfg.setDirectoryForTemplateLoading(templateDirectory);  // non-file-system sources are possible too: see setTemplateLoader();
    cfg.setDefaultEncoding("UTF-8");
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    cfg.setLogTemplateExceptions(false);
    return cfg;
  }
}
