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
    CASE_CREATED("CaseCreated"),
    CASE_CLOSED("CaseClosed"),
    ACTION_UPDATED("ActionUpdated"),
    ACTION_COMPLETED("ActionCompleted"),
    ACTION_CREATED("ActionCreated"),
    GENERAL_ENQUIRY ("General Enquiry"),
    COMPLAINT("Complaint"),
    SURVEY_ENQUIRY ("Survey Enquiry"),
    ADDRESS_DETAILS_INCORRECT ("Address Details Incorrect"),
    CLASSIFICATION_INCORRECT ("Classification Incorrect"),
    REFUSAL("Refusal"),
    REQUEST_FOR_FULFILLMENT ("Request for Fulfilment"),
    TECHNICAL_QUERY ("Technical Query"),
    MISCELLANEOUS("Miscellaneous"),
    PENDING("Pending"),
    CLOSED("Closed"),
    COMPLAINT_ESCALATED("Complaint - Escalated"),
    GENERAL_ENQUIRY_ESCALATED("General Enquiry - Escalated"),
    SURVEY_ENQUIRY_ESCALATED("Survey Enquiry - Escalated"),
    UNDELIVERABLE("Undeliverable"),
    QUESTIONNAIRE_RESPONSE("QuestionnaireResponse"),
    ACTION_CANCELLATION_COMPLETED("ActionCancellationCompleted"),
    ACTION_CANCELLATION_CREATED("ActionCancellationCreated");

    private String label;

    private CategoryName(String label) {
      this.label = label;
    }

    public String getLabel() {
      return label;
    }

    public static CategoryName getEnumByLabel(String label) {
      for (CategoryName e : CategoryName.values()) {
        if (label.equals(e.label)) {
          return e;
        }
      }
      return null;
    }
  }

  private String name;

  private String description;

  private String role;

  private String generatedActionType;

  private Boolean closeCase;

  private Boolean manual;

}
