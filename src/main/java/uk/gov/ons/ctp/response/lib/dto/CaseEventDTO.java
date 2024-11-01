package uk.gov.ons.ctp.response.lib.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Date;
import java.util.Map;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class CaseEventDTO {

  private static final int DESC_MAX = 350;
  private static final int DESC_MIN = 2;
  private static final int CREATED_BY_MAX = 50;
  private static final int CREATED_BY_MIN = 2;

  private Date createdDateTime;

  @NotNull private CategoryDTO.CategoryName category;

  private String subCategory;

  @NotNull
  @Size(min = CREATED_BY_MIN, max = CREATED_BY_MAX)
  private String createdBy;

  @NotNull
  @Size(min = DESC_MIN, max = DESC_MAX)
  private String description;

  private Map<String, String> metadata;
}
