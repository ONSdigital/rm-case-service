package uk.gov.ons.ctp.response.action.export.config;

import lombok.Data;

/**
 * Config for ExportScheduler
 *
 */
@Data
public class ExportSchedule {
  private String cronExpression;

}
