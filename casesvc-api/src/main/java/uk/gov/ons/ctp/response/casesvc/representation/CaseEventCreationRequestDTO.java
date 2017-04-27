package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import static uk.gov.ons.ctp.common.time.DateTimeUtil.DATE_FORMAT_IN_JSON;

/**
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseEventCreationRequestDTO {

  private static final int DESC_MAX = 350;
  private static final int DESC_MIN = 2;
  private static final int CREATED_BY_MAX = 50;
  private static final int CREATED_BY_MIN = 2;

  @JsonFormat(pattern = DATE_FORMAT_IN_JSON)
  private Date createdDateTime;

  private Integer caseEventId;

  private Integer caseId;

  @NotNull
  private CategoryDTO.CategoryType category;

  private String subCategory;

  @NotNull @Size(min = CREATED_BY_MIN, max = CREATED_BY_MAX)
  private String createdBy;

  @NotNull @Size(min = DESC_MIN, max = DESC_MAX)
  private String description;
  
  private CaseCreationRequestDTO caseCreationRequest;
}
