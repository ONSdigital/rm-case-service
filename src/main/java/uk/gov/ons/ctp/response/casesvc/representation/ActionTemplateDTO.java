package uk.gov.ons.ctp.response.casesvc.representation;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
/** Domain model object to represent the Action Template */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@JsonIgnoreProperties(ignoreUnknown = true)
public class ActionTemplateDTO {
    public enum Handler {
        EMAIL,
        LETTER
    }
    @NotNull
    private String name;
    @NotNull
    private String description;
    @NotNull
    private String tag;
    @NotNull
    private Handler handler;
    private String prefix;
}
