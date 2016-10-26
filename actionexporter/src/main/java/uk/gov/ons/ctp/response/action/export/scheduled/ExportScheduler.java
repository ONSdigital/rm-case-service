package uk.gov.ons.ctp.response.action.export.scheduled;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;

import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

/**
 * This class will be responsible for the scheduling of export actions
 *
 */
@Named
public class ExportScheduler implements HealthIndicator {

  @Inject
  private TransformationService transformationService;

  @Inject
  private SftpServicePublisher sftpService;

  @Inject
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
    SftpMessage message = transformationService.processActionRequests();
    message.getOutputStreams().forEach((fileName, stream) -> {
      sftpService.sendMessage(fileName, message.getActionRequestIds(fileName), stream);
    });
  }
}
