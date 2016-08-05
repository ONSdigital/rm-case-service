package uk.gov.ons.ctp.response.casesvc.message.impl;

import javax.inject.Named;

import org.springframework.integration.annotation.Publisher;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.NotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/**
 * Service implementation responsible for publishing case lifecycle events to
 * notification channel
 *
 */
@Named
@Slf4j
public class NotificationPublisherImpl implements NotificationPublisher {

  @Override
  @Publisher(channel = "caseNotificationOutbound")
  public CaseNotification sendNotification(CaseNotification caseNotification) {
    log.debug("Entering sendNotification for caseId {}", caseNotification.getCaseId());
    return caseNotification;
  }

}
