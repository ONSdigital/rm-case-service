package uk.gov.ons.ctp.response.casesvc.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class ActionTemplateDTO {
    public enum Handler {
        EMAIL,
        LETTER
    }

    private String name;
    private String description;
    private String event_tag_mapping;
    private Handler handler;
    private String prefix;
}
