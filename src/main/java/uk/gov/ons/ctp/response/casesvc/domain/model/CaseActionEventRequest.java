package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_action_event_request", schema = "casesvc")
public class CaseActionEventRequest implements Serializable {
  private static final long serialVersionUID = 7779260895016862376L;

  public enum ActionEventRequestStatus {
    INPROGRESS,
    COMPLETED,
    FAILED,
    RETRY
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private BigInteger id;

  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  @Column(name = "event_tag")
  private String eventTag;

  @Column(name = "process_event_requested_time")
  private Timestamp requestedTimestamp;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @NotNull
  private CaseActionEventRequest.ActionEventRequestStatus status;
}
