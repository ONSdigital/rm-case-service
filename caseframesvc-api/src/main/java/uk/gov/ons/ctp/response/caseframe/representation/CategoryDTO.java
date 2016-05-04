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

  public enum CategoryName {
    QUESTIONNAIRE_RESPONSE("QuestionnaireResponse"), CASE_CREATED("CaseCreated"), CASE_CLOSED(
        "CaseClosed"), ACTION_CREATED(
            "ActionCreated"), ACTION_UPDATED("ActionUpdated"), ACTION_COMPLETED("ActionCompleted");
    private String m_label;

    private CategoryName(String label) {
      m_label = label;
    }

    public String getLabel() {
      return m_label;
    }
  }

  private String name;

  private String description;

  private String role;

  private String generatedActionType;

  private Boolean closeCase;

  private Boolean manual;

}
