package uk.gov.ons.ctp.response.casesvc.message.impl;

import java.util.List;

import javax.inject.Named;

import org.springframework.integration.annotation.Publisher;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.message.CaseNotificationPublisher;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;
import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotifications;

/**
 * Service implementation responsible for publishing case lifecycle events to
 * notification channel
 *
 */
@Named
@Slf4j
public class CaseNotificationPublisherImpl implements CaseNotificationPublisher {

  @Override
  @Publisher(channel = "caseNotificationOutbound")
  public CaseNotifications sendNotifications(List<CaseNotification> caseNotificationList) {
    log.debug("Entering sendNotifications with {} CaseNotification ", caseNotificationList.size());
    CaseNotifications caseNotifications = new CaseNotifications();
    caseNotifications.getCaseNotifications().addAll(caseNotificationList);
    return caseNotifications;
  }
}
