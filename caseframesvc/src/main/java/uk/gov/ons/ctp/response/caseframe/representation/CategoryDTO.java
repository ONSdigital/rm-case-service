package uk.gov.ons.ctp.response.caseframe.representation;

import lombok.Data;

/**
 * The object to represent a Category
 */
@Data
public class CategoryDTO {

  private String name;

  private String description;

  private String role;

  private String generatedActionType;

  private Boolean closeCase;

  private Boolean manual;

}
