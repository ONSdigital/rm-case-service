package uk.gov.ons.ctp.response.action.export.service.impl;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.ExportMessage;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateMappingService;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

/**
 * The implementation of TransformationService
 */
@Named
@Slf4j
public class TransformationServiceImpl implements TransformationService {

  private static final String DATE_FORMAT_IN_FILE_NAMES = "ddMMyyyy_HH:mm";
  private static final String TEMPLATE_MAPPING = "templateMapping";

  @Inject
  private ActionRequestRepository actionRequestRepo;

  @Inject
  private TemplateService templateService;

  @Inject
  private TemplateMappingService templateMappingService;

  @Override
  public ExportMessage processActionRequests() {
    List<ActionRequestDocument> requests = actionRequestRepo.findByDateSentIsNull();
    return buildExportMessage(requests);
  }

  @Override
  public ExportMessage processActionRequest(ActionRequestDocument actionRequestDocument) {
    List<ActionRequestDocument> requests = new ArrayList<>();
    requests.add(actionRequestDocument);
    return buildExportMessage(requests);
  }

  /**
   * Produces ExportMessage with stream objects and list of ActionRequest Ids.
   * @param actionRequestDocumentList the list to be processed
   * @return ExportMessage with stream objects and list of ActionRequest Ids.
   */
  private ExportMessage buildExportMessage(List<ActionRequestDocument> actionRequestDocumentList) {
    Map<String, List<String>> actionIds = new HashMap();
    Map<String, ByteArrayOutputStream> outputStreams = new HashMap();
    ExportMessage message = new ExportMessage(actionIds, outputStreams);

    if (actionRequestDocumentList.isEmpty()) {
      log.warn("No Action Export requests to process.");
      return message;
    }

    Map<String, List<ActionRequestDocument>> templateRequests = actionRequestDocumentList.stream()
            .collect(Collectors.groupingBy(ActionRequestDocument::getActionType));
    Map<String, String> mapping = templateMappingService.retrieveMapFromTemplateMappingDocument(TEMPLATE_MAPPING);
    String timeStamp = new SimpleDateFormat(DATE_FORMAT_IN_FILE_NAMES).format(Calendar.getInstance().getTime());
    templateRequests.forEach((actionType, actionRequests) -> {
      if (mapping.containsKey(actionType)) {
        try {
          outputStreams.put(actionType + "_" + timeStamp + ".csv",
                  templateService.stream(actionRequests, mapping.get(actionType)));
          List<String> addActionIds = new ArrayList<String>();
          actionIds.put(actionType + "_" + timeStamp + ".csv", addActionIds);
          actionRequests.forEach((actionRequest) -> {
            addActionIds.add(actionRequest.getActionId().toString());
          });
        } catch (CTPException e) {
          log.error("Error generating actionType : {}. {}", actionType, e.getMessage());
        }
      } else {
        log.warn("No mapping for actionType : {}.", actionType);
      }
    });
    return message;
  }
}
