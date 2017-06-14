package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import uk.gov.ons.ctp.common.util.EnumUtils;
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
   * enum for category name
   */
  public enum CategoryName {
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
    ACCESS_CODE_AUTHENTICATION_ATTEMPT,
    INCORRECT_ESCALATION,
    MISCELLANEOUS,
    FIELD_EMERGENCY_ESCALATED,
    CLOSE_ESCALATION,
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
    H_INDIVIDUAL_RESPONSE_REQUESTED,
    H_INDIVIDUAL_REPLACEMENT_IAC_REQUESTED,
    H_INDIVIDUAL_PAPER_REQUESTED,
    RESPONDENT_ENROLLED,
    TRANSLATION_POLISH,
    TRANSLATION_CANTONESE,
    TRANSLATION_SOMALI,
    TRANSLATION_MANDARIN,
    TRANSLATION_BENGALI,
    TRANSLATION_PUNJABI_SHAHMUKI,
    TRANSLATION_PUNJABI_GURMUKHI,
    TRANSLATION_LITHUANIAN,
    TRANSLATION_GUJARATI,
    TRANSLATION_TURKISH,
    TRANSLATION_ARABIC,
    TRANSLATION_URDU,
    TRANSLATION_PORTUGUESE,
    TRANSLATION_SPANISH,
    RESPONDENT_ACCOUNT_CREATED,
    COLLECTION_INSTRUMENT_DOWNLOADED;

    @JsonCreator
    public static CategoryName fromValue(String name) {
      return EnumUtils.getEnumFromString(CategoryName.class, name);
    }
    
    public static Optional<CategoryName> fromString(String name) {
      return EnumUtils.getOptionalEnumFromString(CategoryName.class, name);
    }
  }

  private String group;

  private CategoryName name;

  private String longDescription;

  private String shortDescription;

  private String role;
}
