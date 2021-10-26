package uk.gov.ons.ctp.response.casesvc.representation.action;

import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate.Handler;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class LetterEntry {
  private UUID actionCaseId;
  private String actionTemplateType;
  private Handler actionTemplateHandler;
  private String sampleUnitRef;
  private String iac;
  private String caseGroupStatus;
  private String enrolmentStatus;
  private String respondentStatus;
  private Contact contact;
  private String region;
}
