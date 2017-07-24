package uk.gov.ons.ctp.response.casesvc.representation;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.UUID;

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

  @NotNull @Size(min = DESC_MIN, max = DESC_MAX)
  @ApiModelProperty(required = true)
  private String description;

  @NotNull
  @ApiModelProperty(required = true)
  private CategoryDTO.CategoryName category;

  @NotNull @Size(min = CREATED_BY_MIN, max = CREATED_BY_MAX)
  @ApiModelProperty(required = true)
  private String createdBy;

  private String subCategory;

  private UUID partyId;
}
