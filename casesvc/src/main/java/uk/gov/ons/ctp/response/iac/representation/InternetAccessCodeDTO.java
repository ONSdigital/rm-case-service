package uk.gov.ons.ctp.response.iac.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

/**
 * Domain model object.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class InternetAccessCodeDTO {

  private String code;

  private Boolean active;

  private String createdBy;

  private Timestamp createdDateTime;

  private String updatedBy;

  private Timestamp updatedDateTime;

  private Timestamp lastUsedDateTime;
}
