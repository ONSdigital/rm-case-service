/**
 * 
 */
package uk.gov.ons.ctp.response.casesvc.message;

import org.springframework.integration.annotation.Publisher;

import uk.gov.ons.ctp.response.casesvc.message.notification.CaseNotification;

/**
 * Service responsible for publishing case lifecycle events to notification
 * channel
 *
 */
public interface NotificationPublisher {

	  /**
	   * To put a CaseNotification on the outbound channel caseNotification
	   * @param caseNotification the CaseNotification to put on the outbound channel
	   * @return CaseNotification
	   */
	  CaseNotification sendNotification(CaseNotification caseNotification);
}
