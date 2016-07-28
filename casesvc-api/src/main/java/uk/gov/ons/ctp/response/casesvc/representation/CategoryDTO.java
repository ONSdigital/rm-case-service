package uk.gov.ons.ctp.response.casesvc.representation;

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

  /**
   * enum for category type
   */
  public enum CategoryName {
    CASE_CREATED("CaseCreated"),
    CASE_CLOSED("CaseClosed"),
    ACTION_UPDATED("ActionUpdated"),
    ACTION_COMPLETED("ActionCompleted"),
    ACTION_CREATED("ActionCreated"),
    GENERAL_ENQUIRY("General Enquiry"),
    COMPLAINT("Complaint"),
    SURVEY_ENQUIRY("Survey Enquiry"),
    ADDRESS_DETAILS_INCORRECT("Address Details Incorrect"),
    CLASSIFICATION_INCORRECT("Classification Incorrect"),
    REFUSAL("Refusal"),
    REQUEST_FOR_FULFILLMENT("Request for Fulfilment"),
    TECHNICAL_QUERY("Technical Query"),
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

    /**
     * constructor
     * @param theLabel the ... label
     */
    CategoryName(String theLabel) {
      this.label = theLabel;
    }

    /**
     * get the label
     * @return the label
     */
    public String getLabel() {
      return label;
    }

    /**
     * get the enum for a given label
     * @param label this is the label
     * @return this is the enum!
     */
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
