package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;

import java.io.File;
import java.io.OutputStream;
import java.util.List;

public interface TransformationService {
  /**
   * This produces a csv file for all our action requests
   *
   * @param actionRequestDocumentList the list of action requests
   * @param path the full file path. An example is /tmp/csv/forPrinter.csv
   */
  File fileMe(List<ActionRequestDocument> actionRequestDocumentList, String path);

  OutputStream streamMe(List<ActionRequestDocument> actionRequestDocumentList);
}

