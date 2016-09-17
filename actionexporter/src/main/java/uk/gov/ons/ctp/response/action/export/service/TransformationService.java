package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.List;

/**
 * Service to transform lists of ActionRequests so they can be exported to file, stream, etc.
 */
public interface TransformationService {
  /**
   * This produces a csv file for all our action requests
   *
   * @param actionRequestDocumentList the list of action requests
   * @param templateName the FreeMarker template to use
   * @param path the full file path. An example is /tmp/csv/forPrinter.csv
   * @return the file
   */
  File fileMe(List<ActionRequestDocument> actionRequestDocumentList, String templateName, String path)
          throws CTPException;

  /**
   * This produces a stream for all our action requests
   *
   * @param actionRequestDocumentList the list of action requests
   * @param templateName the FreeMarker template to use
   * @return the stream
   */
  ByteArrayOutputStream streamMe(List<ActionRequestDocument> actionRequestDocumentList, String templateName)
          throws CTPException;
}

