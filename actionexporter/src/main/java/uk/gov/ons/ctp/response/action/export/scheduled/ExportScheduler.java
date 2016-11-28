package uk.gov.ons.ctp.response.action.export.scheduled;

import static uk.gov.ons.ctp.response.action.export.service.impl.TemplateMappingServiceImpl.TEMPLATE_MAPPING;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.scheduling.annotation.Scheduled;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.ExportMessage;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMapping;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

/**
 * This class will be responsible for the scheduling of export actions
 *
 */
@Named
@Slf4j
public class ExportScheduler implements HealthIndicator {

  private static final String DATE_FORMAT_IN_FILE_NAMES = "ddMMyyyy_HHmm";

  @Inject
  private TransformationService transformationService;

  @Inject
  private TemplateMappingService templateMappingService;

  @Inject
  private SftpServicePublisher sftpService;

  @Inject
  private ActionRequestService actionRequestService;

  @Inject
  private ExportInfo exportInfo;

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

    try {
      log.info("Scheduled run start");
      List<TemplateMapping> templates = templateMappingService.retrieveTemplateMapping(TEMPLATE_MAPPING);

      // Warn if Mapping document cannot deal with all ActionRequests stored
      List<String> storedActionTypes = actionRequestService.retieveActionTypes();
      List<String> mappedActionTypes = templates.stream().map(TemplateMapping::getActionType)
          .collect(Collectors.toList());
      storedActionTypes.forEach((actionType) -> {
        if (!mappedActionTypes.contains(actionType)) {
          log.warn("No mapping for actionType : {}.", actionType);
        }
      });

      // Process templateMappings by file, have to as may be many actionTypes in
      // one file. Does not assume actionTypes in the same file use the same
      // template even so.
      String timeStamp = new SimpleDateFormat(DATE_FORMAT_IN_FILE_NAMES).format(Calendar.getInstance().getTime());
      templateMappingService.retrieveTemplateMappingByFilename(TEMPLATE_MAPPING)
          .forEach((fileName, templatemappings) -> {
            ExportMessage message = new ExportMessage();
            // process Collection of templateMappings
            templatemappings.forEach((templateMapping) -> {
              List<ActionRequestDocument> requests = actionRequestService
                  .findByDateSentIsNullAndActionType(templateMapping.getActionType());
              if (requests.isEmpty()) {
                log.info("Scheduled run no requests for actionType {} to process", templateMapping.getActionType());
              } else {
                try {
                  transformationService.processActionRequests(message, requests);
                } catch (CTPException e) {
                  // Error retrieving TemplateMapping in transformationService
                  log.error("Scheduled run error transforming ActionRequests");
                }
              }
            });
            if (!message.isEmpty()) {
              sftpService.sendMessage(fileName + "_" + timeStamp + ".csv", message.getMergedActionRequestIds(),
                  message.getMergedOutputStreams());
            }
          });
    } catch (CTPException e) {
      // Error retrieving TemplateMapping
      log.error("Scheduled run error: {}", e.getMessage());
    }
  }
}
