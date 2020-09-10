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
    ACCESS_CODE_AUTHENTICATION_ATTEMPT,
    ACTION_CANCELLATION_COMPLETED,
    ACTION_CANCELLATION_CREATED,
    ACTION_COMPLETED,
    ACTION_CREATED,
    ACTION_UPDATED,
    ADDRESS_OCCUPIED_NO_RESIDENT,
    CASE_CREATED,
    COLLECTION_INSTRUMENT_DOWNLOADED,
    COLLECTION_INSTRUMENT_ERROR,
    COMMUNAL_ESTABLISHMENT_INSTITUTION,
    COMPLETED_BY_PHONE,
    COMPLY_IN_DIFFERENT_COLLECTION_MODE,
    DECEASED,
    DISABLE_RESPONDENT_ENROLMENT,
    DWELLING_OF_FOREIGN_SERVICE_PERSONNEL_DIPLOMATS,
    EQ_LAUNCH,
    FULL_INTERVIEW_REQUEST_DATA_DELETED,
    FULL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT,
    GENERATE_ENROLMENT_CODE,
    ILL_AT_HOME,
    IN_HOSPITAL,
    LACK_OF_COMPUTER_INTERNET_ACCESS,
    LANGUAGE_DIFFICULTIES,
    LEGITIMACY_CONCERNS,
    NON_RESIDENTIAL_ADDRESS,
    NO_ACTIVE_ENROLMENTS,
    NO_LONGER_REQUIRED,
    NO_PERSON_IN_ELIGIBLE_AGE_RANGE,
    NO_TRACE_OF_ADDRESS,
    OFFLINE_RESPONSE_PROCESSED,
    OTHER_CIRCUMSTANTIAL_REFUSAL,
    OTHER_OUTRIGHT_REFUSAL,
    PARTIAL_INTERVIEW_REQUEST_DATA_DELETED,
    PARTIAL_INTERVIEW_REQUEST_DATA_DELETED_INCORRECT,
    PHYSICALLY_OR_MENTALLY_UNABLE,
    PRIVACY_DATA_CONFIDENTIALITY_CONCERNS,
    REQUEST_TO_COMPLETE_IN_ALTERNATIVE_FORMAT,
    RESPONDENT_ACCOUNT_CREATED,
    RESPONDENT_EMAIL_AMENDED,
    RESPONDENT_ENROLED,
    SECURE_MESSAGE_SENT,
    SUCCESSFUL_RESPONSE_UPLOAD,
    TOO_BUSY,
    UNSUCCESSFUL_RESPONSE_UPLOAD,
    VACANT_OR_EMPTY,
    VERIFICATION_CODE_SENT,
    WRONG_ADDRESS;

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
