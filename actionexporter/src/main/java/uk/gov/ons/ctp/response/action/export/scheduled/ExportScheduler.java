package uk.gov.ons.ctp.response.action.export.scheduled;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.inject.Named;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.Data;

/**
 * This class will be responsible for the scheduling of export actions
 *
 */
@Named
public class ExportScheduler implements HealthIndicator {

  /**
   * Info returned to Spring boot actuator available at health endpoint as
   * configured in application under management e.g. /mgmt/health
   *
   */
  @Data
  private class ExportInfo {
    private String lastRunTime;
    private List<String> callTimes = new ArrayList<>();

    /**
     * Add last call execution details
     * @param date Details of last scheduled export action
     *
     */
    public void addCall(String date) {
      lastRunTime = date;
      callTimes.add(date);
    }
  };

  private ExportInfo exportInfo = new ExportInfo();

  @Override
  public Health health() {
    return Health.up()
        .withDetail("exportInfo", exportInfo)
        .build();
  }

  /**
   * Carry out scheduled actions according to configured cron expression
   *
   */
  @Scheduled(cron = "#{appConfig.exportSchedule.cronExpression}")
  public void scheduleExport() {
    DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    exportInfo.addCall(dateFormat.format(Calendar.getInstance().getTime()));
  }
}
