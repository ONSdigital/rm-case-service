package uk.gov.ons.ctp.response.casesvc.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;
import net.sourceforge.cobertura.CoverageIgnore;

/**
 * Application Config bean
 *
 */
@CoverageIgnore
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
}
