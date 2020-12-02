package uk.gov.ons.ctp.response.casesvc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;

import javax.persistence.*;
import java.io.Serializable;


@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "action_template", schema = "casesvc")
public class ActionTemplate implements Serializable {
    
    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "event_tag_mapping")
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(name = "handler")
    private ActionTemplateDTO.Handler handler;

    @Column(name = "prefix")
    private String prefix;
}
