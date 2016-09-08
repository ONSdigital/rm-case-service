package uk.gov.ons.ctp.response.action.export.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * The apps main holder for centralized config read from application.yml or env
 * vars
 *
 */
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private ExportSchedule exportSchedule;

}
