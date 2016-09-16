package uk.gov.ons.ctp.response.action.export.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class FreeMarkerTemplateDTO {
  @NotNull
  private String name;
  private Date dateModified;
}
