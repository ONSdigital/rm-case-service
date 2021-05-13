package uk.gov.ons.ctp.response.casesvc.config;

import lombok.Data;

@Data
public class GCP {
  String project;
  String caseNotificationTopic;
}
