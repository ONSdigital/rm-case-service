package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;

/** Config POJO for GCP params */
@Data
public class GCP {
  String project;
  String receiptSubscription;
  String caseNotificationTopic;
  String caseNotificationSubscription;
}
