package uk.gov.ons.ctp.response.action.export.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;

/**
 * Service responsible for dealing with ContentDocuments stored in Mongo,
 * presently Freemarker template and mapping documents.
 */
public interface DocumentService {
  /**
   * To store a ContentDocument
   *
   * @param contentDocumentName the ContentDocument name
   * @param fileContents the ContentDocument content
   * @return the ContentDocument stored
   * @throws CTPException if the ContentDocument content is empty
   */
  ContentDocument storeContentDocument(String contentDocumentName, InputStream fileContents) throws CTPException;

  /**
   * To retrieve a given ContentDocument
   *
   * @param contentDocumentName the ContentDocument name to be retrieved
   * @return the given ContentDocument
   */
  ContentDocument retrieveContentDocument(String contentDocumentName);

  /**
   * To retrieve all ContentDocuments
   *
   * @return a list of ContentDocuments
   */
  List<ContentDocument> retrieveAllContentDocuments();

  /**
   * To retrieve the mapping of ActionRequests to templates
   *
   * @param mappingName Name of mapping document to retrieve
   * @return Map of actionType to template name
   */
  Map<String, String> retrieveMapping(String mappingName);

  /**
   * To clear the FreeMarker configuration's template cache
   */
  void clearTemplateCache();
}
