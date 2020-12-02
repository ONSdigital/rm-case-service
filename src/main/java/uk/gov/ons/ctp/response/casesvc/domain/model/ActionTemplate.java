package uk.gov.ons.ctp.response.casesvc.domain.model;

import lombok.*;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "action_template", schema = "casesvc")
public class ActionTemplate implements Serializable {
    private static final long serialVersionUID = 7778360895016862376L;
    @Id
    @Column(name = "name")
    @NotNull
    private String name;

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
