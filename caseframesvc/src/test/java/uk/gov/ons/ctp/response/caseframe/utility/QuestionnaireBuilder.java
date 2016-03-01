package uk.gov.ons.ctp.response.caseframe.utility;

import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;

import java.sql.Timestamp;

/**
 * Created by philippe.brossier on 2/26/16.
 */
public class QuestionnaireBuilder {

  public static final String QUESTIONNAIRE_SET = "set";
  public static final String QUESTIONNAIRE_STATUS = "status";
  public static final Timestamp QUESTIONNAIRE_DISPATCHDATE_TIMESTAMP = Timestamp.valueOf("2016-02-26 18:30:00");
  public static final String QUESTIONNAIRE_DISPATCHDATE_VALUE = "2016-02-26T18:30:00.000+0000";
  public static final Timestamp QUESTIONNAIRE_RECEIPTDATE_TIMESTAMP = Timestamp.valueOf("2017-02-26 18:30:00");
  public static final String QUESTIONNAIRE_RECEIPTDATE_VALUE = "2017-02-26T18:30:00.000+0000";
  public static final Timestamp QUESTIONNAIRE_RESPONSEDATE_TIMESTAMP = Timestamp.valueOf("2018-02-26 18:30:00");
  public static final String QUESTIONNAIRE_RESPONSEDATE_VALUE = "2018-02-26T18:30:00.000+0000";

  private Integer caseid;
  private Integer id;
  private String iac;

  private QuestionnaireBuilder(){}

  public QuestionnaireBuilder caseid(Integer caseid) {
    this.caseid = caseid;
    return this;
  }

  public QuestionnaireBuilder id(Integer id) {
    this.id = id;
    return this;
  }

  public QuestionnaireBuilder iac(String iac) {
    this.iac = iac;
    return this;
  }

  public Questionnaire buildQuestionnaire() {
    Questionnaire questionnaire = new Questionnaire();
    questionnaire.setQuestionnaireId(this.id);
    questionnaire.setIac(this.iac);
    questionnaire.setCaseId(this.caseid);
    questionnaire.setQuestionnaireStatus(QUESTIONNAIRE_STATUS);
    questionnaire.setQuestionSet(QUESTIONNAIRE_SET);
    questionnaire.setDispatchDateTime(QUESTIONNAIRE_DISPATCHDATE_TIMESTAMP);
    questionnaire.setReceiptDateTime(QUESTIONNAIRE_RECEIPTDATE_TIMESTAMP);
    questionnaire.setResponseDateTime(QUESTIONNAIRE_RESPONSEDATE_TIMESTAMP);
    return questionnaire;
  }

  public static QuestionnaireBuilder questionnaire() {
    return new QuestionnaireBuilder();
  }
}
