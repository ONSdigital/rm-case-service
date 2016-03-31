package uk.gov.ons.ctp.response.caseframe.representation;

import java.util.Date;

import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 */
@Data
public class CaseEventDTO {
  private Date createdDateTime;

  private Integer caseEventId;

  private Integer caseId;

  @NotNull @Size(min=1, max=40)
  private String category;
  
  private String subCategory;

  @NotNull @Size(min=2, max=20)
  private String createdBy;

  @NotNull @Size(min=2, max=100)
  private String description;
}
