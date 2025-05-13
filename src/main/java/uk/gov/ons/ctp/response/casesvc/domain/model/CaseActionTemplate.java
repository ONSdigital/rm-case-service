package uk.gov.ons.ctp.response.casesvc.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_action_template", schema = "casesvc")
public class CaseActionTemplate implements Serializable {
  public enum Handler {
    EMAIL,
    LETTER
  }

  private static final long serialVersionUID = 7778360895016862376L;

  @Id
  @Column(name = "type")
  @NotNull
  private String type;

  @Column(name = "description")
  @NotNull
  private String description;

  @Column(name = "event_tag")
  @NotNull
  private String tag;

  @Enumerated(EnumType.STRING)
  @Column(name = "handler")
  @NotNull
  private Handler handler;

  @Column(name = "prefix")
  private String prefix;
}
