package uk.gov.ons.ctp.response.casesvc.representation.action;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import javax.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

  @NotNull private String type;
  @NotNull private String description;
  @NotNull private String tag;
  @NotNull private Handler handler;
  private String prefix;
}
