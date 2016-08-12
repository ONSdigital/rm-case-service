package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;

/**
 * Application Config bean for batching of Notification messages
 *
 */
@Data
public class NotificationPubl {
  private Integer notificationMax;
}
