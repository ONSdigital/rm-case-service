package uk.gov.ons.ctp.response.action.export.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * Representation of TemplateDocument
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class TemplateDocumentDTO {
  @NotNull
  private String name;
  private String content;
  private Date dateModified;
}
