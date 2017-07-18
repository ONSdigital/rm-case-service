package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/**
 * Service responsible for publishing case lifecycle events to notification
 * channel
 *
 */
public interface CaseNotificationPublisher {

  /**
   * To put one CaseNotification on the outbound channel caseNotificationOutbound
   * @param caseNotification the CaseNotification to put on the outbound channel
   */
  void sendNotification(CaseNotification caseNotification);
}
