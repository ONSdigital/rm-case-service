package uk.gov.ons.ctp.response.action.export.message.impl;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.integration.annotation.MessageEndpoint;
import org.springframework.integration.annotation.Publisher;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.file.FileHeaders;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.messaging.support.GenericMessage;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;
import uk.gov.ons.ctp.response.action.export.scheduled.ExportInfo;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;

/**
 * Service implementation responsible for publishing transformed ActionRequests
 * via sftp. See Spring Integration flow for details of sftp outbound channel.
 *
 */
@Named
@MessageEndpoint
@Slf4j
public class SftpServicePublisherImpl implements SftpServicePublisher {

  private static final String ACTION_LIST = "list_actionIds";

  @Inject
  private ActionRequestService actionRequestService;

  @Inject
  private ExportInfo exportInfo;

  @Override
  @Publisher(channel = "sftpOutbound")
  public byte[] sendMessage(@Header(FileHeaders.REMOTE_FILE) String filename,
      @Header(ACTION_LIST) List<String> actionIds,
      ByteArrayOutputStream stream) {
    return stream.toByteArray();
  }

  @SuppressWarnings("unchecked")
  @Override
  @ServiceActivator(inputChannel = "sftpSuccessProcess")
  public void sftpSuccessProcess(GenericMessage<GenericMessage<byte[]>> message) {
    List<String> actionIds = (List<String>) message.getPayload().getHeaders().get(ACTION_LIST);
    Date now = new Date();
    actionIds.forEach((actionId) -> {
      ActionRequestDocument actionRequest =
              actionRequestService.retrieveActionRequestDocument(new BigInteger(actionId));
      actionRequest.setDateSent(now);
      ActionRequestDocument saved = actionRequestService.save(actionRequest);
      if (saved == null) {
        log.error("ActionRequestDocument {} failed to update DateSent", actionRequest.getActionId());
      }
    });
    log.info("Sftp transfer complete for file {}", message.getPayload().getHeaders().get(FileHeaders.REMOTE_FILE));
    exportInfo.addOutcome((String) message.getPayload().getHeaders().get(FileHeaders.REMOTE_FILE) + " transferred with "
        + Integer.toString(actionIds.size()) + " requests.");
  }

  @Override
  @ServiceActivator(inputChannel = "sftpFailedProcess")
  public void sftpFailedProcess(ErrorMessage message) {
    log.error("Sftp transfer failed for file {} for action requests {}",
        ((MessagingException) message.getPayload()).getFailedMessage().getHeaders().get(FileHeaders.REMOTE_FILE),
        ((MessagingException) message.getPayload()).getFailedMessage().getHeaders().get(ACTION_LIST));
    exportInfo.addOutcome((String) ((MessagingException) message.getPayload()).getFailedMessage().getHeaders()
        .get(FileHeaders.REMOTE_FILE) + " failed to transfer.");

  }

}
