package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.action.ActionTemplateDTO;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "case_action_template", schema = "casesvc")
public class CaseActionTemplate implements Serializable {
  private static final long serialVersionUID = 7778360895016862376L;

  @Id
  @Column(name = "type")
  @NotNull
  private String type;

  @Column(name = "description")
  @NotNull
  private String description;

  @Column(name = "event_tag_mapping")
  @NotNull
  private String tag;

  @Enumerated(EnumType.STRING)
  @Column(name = "handler")
  @NotNull
  private ActionTemplateDTO.Handler handler;

  @Column(name = "prefix")
  private String prefix;
}
