package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;

import java.util.List;

public interface FileService {
  void fileMe(List<ActionRequestDocument> actionRequestDocumentList);
}

