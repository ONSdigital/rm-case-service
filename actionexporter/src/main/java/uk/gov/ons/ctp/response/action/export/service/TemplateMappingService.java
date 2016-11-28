package uk.gov.ons.ctp.response.action.export.service;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMapping;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;

/**
 * Service responsible for dealing with TemplateMappingDocuments stored in
 * MongoDB
 */
public interface TemplateMappingService {

  /**
   * To store a TemplateMappingDocument
   * 
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
   * @throws CTPException if the TemplateMappingDocument is not found.
   */
  TemplateMappingDocument retrieveTemplateMappingDocument(String templateMappingName) throws CTPException;

  /**
   * To retrieve all TemplateMappingDocuments
   *
   * @return a list of TemplateMappingDocuments
   */
  List<TemplateMappingDocument> retrieveAllTemplateMappingDocuments();

  /**
   * To retrieve a Map of TemplateMappings by filename.
   *
   * @param templateMappingName the TemplateMappingDocument name to be
   *          retrieved.
   * @return the Map of TemplateMappingDocuments by filename.
   */
  Map<String, List<TemplateMapping>> retrieveTemplateMappingByFilename(String templateMappingName) throws CTPException;

  /**
   * To retrieve a Map of TemplateMappings by actionType.
   *
   * @param templateMappingName the TemplateMappingDocument name to be
   *          retrieved.
   * @return the Map of TemplateMappingDocuments by actionType.
   */
  Map<String, TemplateMapping> retrieveTemplateMappingByActionType(String templateMappingName) throws CTPException;

  /**
   * To retrieve TemplateMappings stored in a TemplateMappingDocument
   * 
   * @param templateMappingName the relevant TemplateMappingDocument name
   * @return List of TemplateMappings
   */
  List<TemplateMapping> retrieveTemplateMapping(String templateMappingName) throws CTPException;
}
