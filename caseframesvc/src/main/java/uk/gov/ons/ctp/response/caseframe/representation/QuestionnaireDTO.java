package uk.gov.ons.ctp.response.caseframe.representation;

import java.util.Date;

import lombok.Data;

/**
 *
 */
@Data
public class QuestionnaireDTO {

  private Integer questionnaireId;

  private String iac;

  private Integer caseId;

  private String questionnaireStatus;

  private Date dispatchDateTime;

  private Date responseDateTime;

  private Date receiptDateTime;

  private String questionSet;
}
