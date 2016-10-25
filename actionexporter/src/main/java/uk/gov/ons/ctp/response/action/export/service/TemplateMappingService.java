package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Service responsible for dealing with TemplateMappingDocuments stored in MongoDB
 */
public interface TemplateMappingService {
  /**
   * To store a TemplateMappingDocument
   * @param templateMappingName the TemplateMappingDocument name
   * @param fileContents the TemplateMappingDocument content
   * @return the stored TemplateMappingDocument
   * @throws CTPException if the TemplateMappingDocument content is empty
   */
  TemplateMappingDocument storeTemplateMappingDocument(String templateMappingName, InputStream fileContents)
          throws CTPException;

  /**
   * To retrieve a given TemplateMappingDocument
   *
   * @param templateMappingName the TemplateMappingDocument name to be retrieved
   * @return the given TemplateMappingDocument
   */
  TemplateMappingDocument retrieveTemplateMappingDocument(String templateMappingName);

  /**
   * To retrieve all TemplateMappingDocuments
   *
   * @return a list of TemplateMappingDocuments
   */
  List<TemplateMappingDocument> retrieveAllTemplateMappingDocuments();

  /**
   * To retrieve the underlying map contained in a given TemplateMappingDocument
   * @param templateMappingName the relevant TemplateMappingDocument name
   * @return the underlying map contained in a given TemplateMappingDocument
   */
  Map<String, String> retrieveMapFromTemplateMappingDocument(String templateMappingName);
}
