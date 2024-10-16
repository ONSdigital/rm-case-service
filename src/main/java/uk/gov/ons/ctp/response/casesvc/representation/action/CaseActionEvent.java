package uk.gov.ons.ctp.response.casesvc.representation.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionEventRequest;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class CaseActionEvent {
  public enum EventTag {
    mps,
    go_live,
    reminder,
    reminder2,
    reminder3,
    nudge_email_0,
    nudge_email_1,
    nudge_email_2,
    nudge_email_3,
    nudge_email_4
  }

  @NotNull private UUID collectionExerciseID;
  @NotNull private EventTag tag;
  private CaseActionEventRequest.ActionEventRequestStatus status;
}
