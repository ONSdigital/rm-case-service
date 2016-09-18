package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;

import java.io.InputStream;
import java.util.List;

public interface DocumentService {
  /**
   * To store a ContentDocument
   * @param contentDocumentName the ContentDocument name
   * @param fileContents the ContentDocument content
   * @return the ContentDocument stored
   * @throws CTPException if the ContentDocument content is empty
   */
  ContentDocument storeContentDocument(String contentDocumentName, InputStream fileContents) throws CTPException;

  /**
   * To retrieve a given ContentDocument
   * @param contentDocumentName the ContentDocument name to be retrieved
   * @return the given ContentDocument
   */
  ContentDocument retrieveContentDocument(String contentDocumentName);

  /**
   * To retrieve all ContentDocuments
   * @return a list of ContentDocuments
   */
  List<ContentDocument> retrieveAllContentDocuments();

  /**
   * To clear the FreeMarker configuration's template cache
   */
  void clearTemplateCache();
}
