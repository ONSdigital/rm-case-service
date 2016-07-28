package uk.gov.ons.ctp.response.casesvc.representation;

import java.util.Date;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class QuestionnaireDTO {

  private Integer questionnaireId;

  private String iac;

  private Integer caseId;

  private String state;

  private Date dispatchDateTime;

  private Date responseDateTime;

  private Date receiptDateTime;

  private String questionSet;
}
