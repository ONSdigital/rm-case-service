package uk.gov.ons.ctp.response.action.export.service;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.List;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;

/**
 * Service responsible for dealing with Templates (storage/retrieval in/from MongoDB, templating)
 */
public interface TemplateService {
  /**
   * To store a TemplateDocument
   *
   * @param templateName the TemplateDocument name
   * @param fileContents the TemplateDocument content
   * @return the TemplateDocument stored
   * @throws CTPException if the TemplateDocument content is empty
   */
  TemplateDocument storeTemplateDocument(String templateName, InputStream fileContents)
          throws CTPException;

  /**
   * To retrieve a given TemplateDocument
   *
   * @param templateName the TemplateDocument name to be retrieved
   * @return the given TemplateDocument
   */
  TemplateDocument retrieveTemplateDocument(String templateName);

  /**
   * To retrieve all TemplateDocuments
   *
   * @return a list of TemplateDocuments
   */
  List<TemplateDocument> retrieveAllTemplateDocuments();

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

}
