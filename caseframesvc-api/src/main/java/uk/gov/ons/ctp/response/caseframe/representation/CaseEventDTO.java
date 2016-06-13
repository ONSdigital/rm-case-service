package uk.gov.ons.ctp.response.caseframe.representation;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseEventDTO {

  private static final int DESC_MAX = 350;
  private static final int DESC_MIN = 2;
  private static final int CREATED_BY_MAX = 20;
  private static final int CREATED_BY_MIN = 2;
  private static final int CAT_MAX = 40;
  private static final int CAT_MIN = 1;

  private Date createdDateTime;

  private Integer caseEventId;

  private Integer caseId;

  @NotNull @Size(min = CAT_MIN, max = CAT_MAX)
  private String category;

  private String subCategory;

  @NotNull @Size(min = CREATED_BY_MIN, max = CREATED_BY_MAX)
  private String createdBy;

  @NotNull @Size(min = DESC_MIN, max = DESC_MAX)
  private String description;
}
