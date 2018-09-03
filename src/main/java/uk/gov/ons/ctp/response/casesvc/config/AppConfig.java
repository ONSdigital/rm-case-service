package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.EnableRetry;
import uk.gov.ons.tools.rabbit.Rabbitmq;

/** Application Config bean */
@CoverageIgnore
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
  private ReportSettings reportSettings;
  private Rabbitmq rabbitmq;
  private Logging logging;
}
