package uk.gov.ons.ctp.response.casesvc.representation;

import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.message.notification.NotificationType;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseNotificationDTO {
  protected String sampleUnitId;
  @NotNull protected String caseId;
  protected String actionPlanId;
  protected boolean activeEnrolment;
  @NotNull protected String exerciseId;
  protected String partyId;
  protected String sampleUnitType;
  @NotNull protected NotificationType notificationType;
  protected String sampleUnitRef;
  protected String status;
  protected String iac;
}
