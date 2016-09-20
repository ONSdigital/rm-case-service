package uk.gov.ons.ctp.response.iac.representation;

import javax.validation.constraints.NotNull;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object for representation of the update request body object
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class UpdateInternetAccessCodeDTO {

  @NotNull
  private String updatedBy;


}
