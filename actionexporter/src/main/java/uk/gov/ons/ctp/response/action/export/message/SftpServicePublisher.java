package uk.gov.ons.ctp.response.action.export.message;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.GenericMessage;

/**
 * Service responsible for publishing transformed ActionExport messages to
 * external service using SFTP.
 *
 */
public interface SftpServicePublisher {

  /**
   * To send a stream to the external service via sftp.
   * @param filename on remote system.
   * @param actionIds actionIds being sent.
   * @param stream to send.
   * @return byte array sent as message body.
   */
  byte[] sendMessage(String filename, List<String> actionIds, ByteArrayOutputStream stream);

  /**
   * Operations after success of sftp transfer.
   * @param message instance from SI sftp transfer.
   */
  void sftpSuccessProcess(GenericMessage<GenericMessage<byte[]>> message);

  /**
   * Operations after failure of sftp transfer.
   * @param message instance from SI sftp transfer.
   */
  void sftpFailedProcess(ErrorMessage message);
}
