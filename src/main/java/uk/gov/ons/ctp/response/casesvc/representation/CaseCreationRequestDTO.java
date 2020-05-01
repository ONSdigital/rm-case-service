package uk.gov.ons.ctp.response.casesvc.representation;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** Domain model object */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseCreationRequestDTO {

  //  private Integer caseTypeId;

  //  private Integer actionPlanMappingId;

  private String title;

  private String forename;

  private String surname;

  private String phoneNumber;

  private String emailAddress;
}
