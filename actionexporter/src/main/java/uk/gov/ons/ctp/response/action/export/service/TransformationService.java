package uk.gov.ons.ctp.response.action.export.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;

/**
 * Service to transform lists of ActionRequests so they can be exported to file,
 * stream, etc.
 */
public interface TransformationService {
  /**
   * This produces a csv file for all our action requests.
   *
   * @param actionRequestDocumentList the list of action requests.
   * @param templateName the FreeMarker template to use.
   * @param path the full file path. An example is /tmp/csv/forPrinter.csv.
   * @throws CTPException if problem creating file from template.
   * @return the file.
   */
  File file(List<ActionRequestDocument> actionRequestDocumentList, String templateName, String path)
      throws CTPException;

  /**
   * This produces a stream for all our action requests.
   *
   * @param actionRequestDocumentList the list of action requests.
   * @param templateName the FreeMarker template to use.
   * @throws CTPException if problem creating stream from template.
   * @return the stream.
   */
  ByteArrayOutputStream stream(List<ActionRequestDocument> actionRequestDocumentList, String templateName)
      throws CTPException;

  /**
   * This produces a stream for all action requests not already sent applying the template mapped in the stored mapping
   * document.
   *
   * @return SftpMessage with stream objects and list of ActionRequest Ids.
   */
  SftpMessage processActionRequests();

  /**
   * This produces a stream for the given action request applying the template mapped in the stored mapping document.
   *
   * @param actionRequestDocument the given actionRequest
   * @return SftpMessage with stream object and the given ActionRequest Id.
   */
  SftpMessage processActionRequest(ActionRequestDocument actionRequestDocument);
}
