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
  public enum CategoryType {
    ACTION_CANCELLATION_COMPLETED,
    ACTION_CANCELLATION_CREATED,
    ACTION_COMPLETED,
    ACTION_CREATED,
    ACTION_UPDATED,
    ADDRESS_DETAILS_INCORRECT,
    CASE_CREATED,
    CLASSIFICATION_INCORRECT,
    GENERAL_COMPLAINT,
    GENERAL_COMPLAINT_ESCALATED,
    GENERAL_ENQUIRY,
    GENERAL_ENQUIRY_ESCALATED,
    INCORRECT_ESCALATION,
    MISCELLANEOUS,
    FIELD_EMERGENCY_ESCALATED,
    PENDING,
    FIELD_COMPLAINT_ESCALATED,
    TECHNICAL_QUERY,
    ACCESSIBILITY_MATERIALS,
    PAPER_QUESTIONNAIRE_RESPONSE,
    ONLINE_QUESTIONNAIRE_RESPONSE,
    REFUSAL,
    UNDELIVERABLE,
    HOUSEHOLD_REPLACEMENT_IAC_REQUESTED,
    HOUSEHOLD_PAPER_REQUESTED,
    INDIVIDUAL_RESPONSE_REQUESTED,
    INDIVIDUAL_REPLACEMENT_IAC_REQUESTED,
    INDIVIDUAL_PAPER_REQUESTED,
    TRANSLATION_POLISH,
    TRANSLATION_CANTONESE,
    TRANSLATION_SOMALI,
    TRANSLATION_MANDARIN,
    TRANSLATION_BENGALI,
    TRANSLATION_PUNJABI_SHAHMUKI,
    TRANSLATION_PUNJABI_GURMUKHI,
    TRANSLATION_LITHUANIAN,
    TRANSLATION_GUJERATI,
    TRANSLATION_TURKISH,
    TRANSLATION_ARABIC,
    TRANSLATION_URDU,
    TRANSLATION_PORTUGUESE,
    TRANSLATION_SPANISH
  }

  private Integer categoryId;
  
  private String group;

  private String name;

  private String description;

  private String role;
  
  private CaseDTO.CaseEvent eventType;

  private String generatedActionType;

  private Boolean manual;

}
