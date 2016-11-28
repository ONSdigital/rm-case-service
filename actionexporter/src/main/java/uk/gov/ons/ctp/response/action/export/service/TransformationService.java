package uk.gov.ons.ctp.response.action.export.service;

import java.util.List;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.ExportMessage;

/**
 * Service to transform lists of ActionRequests so they can be exported to an
 * FTP server
 */
public interface TransformationService {

  /**
   * This produces a stream for all action requests not already sent for a
   * particular actionType, applying the template mapped in the stored mapping
   * document.
   * 
   * @param ExportMessage being built
   * @param actionType to process
   * @return ExportMessage with stream objects and list of ActionRequest Ids.
   * @throws CTPException if cannot retrieve TemplateMapping for ActionRequest.
   */
  ExportMessage processActionRequests(ExportMessage message, List<ActionRequestDocument> requests) throws CTPException;

  /**
   * This produces a stream for the given action request applying the template
   * mapped in the stored mapping document.
   * 
   * @param ExportMessage being built
   * @param actionRequestDocument the given actionRequest
   * @return ExportMessage with stream object and the given ActionRequest Id.
   * @throws CTPException if cannot retrieve TemplateMapping for ActionRequest.
   */
  ExportMessage processActionRequest(ExportMessage message, ActionRequestDocument actionRequestDocument)
      throws CTPException;
}
