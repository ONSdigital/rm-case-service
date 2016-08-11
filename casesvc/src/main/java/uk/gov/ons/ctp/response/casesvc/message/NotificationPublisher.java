package uk.gov.ons.ctp.response.casesvc.message;

import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotifications;

/**
 * Service responsible for publishing case lifecycle events to notification
 * channel
 *
 */
public interface NotificationPublisher {

  /**
   * To put CaseNotifications on the outbound channel caseNotificationOutbound
   * 
   * @param caseNotifications the CaseNotifications to put on the outbound
   *          channel
   * @return CaseNotifications
   */
  CaseNotifications sendNotifications(CaseNotifications caseNotifications);
}
