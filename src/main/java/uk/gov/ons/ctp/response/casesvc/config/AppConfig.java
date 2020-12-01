package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import uk.gov.ons.ctp.response.lib.rabbit.Rabbitmq;

/** Application Config bean */
@EnableRetry
@Configuration
@ConfigurationProperties
@Data
public class AppConfig {
  private ActionSvc actionSvc;
  private InternetAccessCodeSvc internetAccessCodeSvc;
  private CaseDistribution caseDistribution;
  private CollectionExerciseSvc collectionExerciseSvc;
  private DataGrid dataGrid;
  private SwaggerSettings swaggerSettings;
  private Rabbitmq rabbitmq;
  private Logging logging;
}
