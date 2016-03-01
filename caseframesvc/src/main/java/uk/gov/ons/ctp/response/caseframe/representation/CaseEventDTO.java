package uk.gov.ons.ctp.response.caseframe.representation;

import java.util.Date;

import lombok.Data;

/**
 */
@Data
public class CaseEventDTO {

  private Integer caseEventId;

  private Integer caseId;

  private String description;

  private String createdBy;

  private Date createdDatetime;

  private String category;

}
