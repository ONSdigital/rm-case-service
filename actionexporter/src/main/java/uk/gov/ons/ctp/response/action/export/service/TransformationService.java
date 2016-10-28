package uk.gov.ons.ctp.response.action.export.service;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;

/**
 * Service to transform lists of ActionRequests so they can be exported to an FTP server
 */
public interface TransformationService {
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
