package uk.gov.ons.ctp.response.casesvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Application Config bean
 *
 */
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private ActionSvc actionSvc;
  private NotificationPubl notificationPubl;
}
