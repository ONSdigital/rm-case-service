package uk.gov.ons.ctp.response.casesvc.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@Builder
@NoArgsConstructor
@Table(name = "case_action_audit_event", schema = "casesvc")
public class CaseActionAuditEvent implements Serializable {
  public enum ActionEventStatus {
    PROCESSED,
    FAILED
  }

  private static final long serialVersionUID = 7890373271889255844L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private BigInteger id;

  @Column(name = "case_id")
  @NotNull
  private UUID caseId;

  @Column(name = "type")
  @NotNull
  private String type;

  @Column(name = "collection_exercise_id")
  private UUID collectionExerciseId;

  @Enumerated(EnumType.STRING)
  @Column(name = "handler")
  @NotNull
  private CaseActionTemplate.Handler handler;

  @Enumerated(EnumType.STRING)
  @Column(name = "status")
  @NotNull
  private ActionEventStatus status;

  @Column(name = "processed_timestamp")
  private Timestamp processedTimestamp;

  @Column(name = "event_tag")
  private String tag;
}
