package uk.gov.ons.ctp.response.casesvc.representation;

import com.fasterxml.jackson.annotation.JsonCreator;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.lib.common.util.EnumUtils;

/** The object to represent a Category */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CategoryDTO {
  /** enum for category name */
  public enum CategoryName {
    ACTION_CANCELLATION_COMPLETED,
    ACTION_CANCELLATION_CREATED,
    ACTION_COMPLETED,
    ACTION_CREATED,
    ACTION_UPDATED,
    CASE_CREATED,
    GENERATE_ENROLMENT_CODE,
    ACCESS_CODE_AUTHENTICATION_ATTEMPT,
    ONLINE_QUESTIONNAIRE_RESPONSE,
    RESPONDENT_ENROLED,
    DISABLE_RESPONDENT_ENROLMENT,
    RESPONDENT_ACCOUNT_CREATED,
    COLLECTION_INSTRUMENT_DOWNLOADED,
    EQ_LAUNCH,
    UNSUCCESSFUL_RESPONSE_UPLOAD,
    SUCCESSFUL_RESPONSE_UPLOAD,
    OFFLINE_RESPONSE_PROCESSED,
    SECURE_MESSAGE_SENT,
    VERIFICATION_CODE_SENT,
    COLLECTION_INSTRUMENT_ERROR,
    COMPLETED_BY_PHONE,
    RESPONDENT_EMAIL_AMENDED,
    NO_LONGER_REQUIRED,
    NO_ACTIVE_ENROLMENTS,
    PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
    PHYSICALLY_OR_MENTALLY_UNABLE,
    LANGUAGE_DIFFICULTIES,
    LACK_OF_COMPUTER_INTERNET_ACCESS,
    TOO_BUSY,
    COMPLY_IN_DIFFERENT_COLLECTION_MODE,
    REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT,
    NON_RESIDENTIAL_ADDRESS,
    ADDRESS_OCCUPIED_NO_RESIDENT,
    COMMUNAL_ESTABLISHMENT_INSTITUTION,
    DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS;

    /**
     * Gets CategoryName enum from string
     *
     * @param name name of Category
     * @return CategoryName category name
     */
    @JsonCreator
    public static CategoryName fromValue(String name) {
      return EnumUtils.getEnumFromString(CategoryName.class, name);
    }

    /**
     * Creates optional of CategoryNames
     *
     * @param name name of Category
     * @return Optional optional of Category Names
     */
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
