package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The object to represent a Category
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CategoryDTO {

  private String name;

  private String description;

  private String role;

  private String generatedActionType;

  private Boolean closeCase;

  private Boolean manual;

}
