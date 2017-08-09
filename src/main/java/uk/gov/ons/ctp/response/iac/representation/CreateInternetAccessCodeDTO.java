package uk.gov.ons.ctp.response.iac.representation;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object for representation of the IAC create request body data
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CreateInternetAccessCodeDTO {

  @NotNull
  private Integer count;

  @NotNull
  private String createdBy;


}
