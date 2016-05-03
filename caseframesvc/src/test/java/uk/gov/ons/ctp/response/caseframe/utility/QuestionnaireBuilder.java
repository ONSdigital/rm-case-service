package uk.gov.ons.ctp.response.caseframe.utility;

import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;

import java.sql.Timestamp;

/**
 * Created by philippe.brossier on 2/26/16.
 */
public final class QuestionnaireBuilder {

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

  /**
   * hidden constructor
   */
  private QuestionnaireBuilder() {
  }

  /**
   * builder method taking postcode
   * @param theCaseId caseId
   * @return the builder
   */
  public QuestionnaireBuilder caseid(final Integer theCaseId) {
    this.caseid = theCaseId;
    return this;
  }

  /**
   * builder method taking id
   * @param theId id
   * @return the builder
   */
  public QuestionnaireBuilder id(final Integer theId) {
    this.id = theId;
    return this;
  }

  /**
   * builder method taking iac
   * @param theIac iac
   * @return the builder
   */
  public QuestionnaireBuilder iac(final String theIac) {
    this.iac = theIac;
    return this;
  }

  /**
   * builder
   * @return a Questionnaire
   */
  public Questionnaire buildQuestionnaire() {
    Questionnaire questionnaire = new Questionnaire();
    questionnaire.setQuestionnaireId(this.id);
    questionnaire.setIac(this.iac);
    questionnaire.setCaseId(this.caseid);
    questionnaire.setState(QUESTIONNAIRE_STATUS);
    questionnaire.setQuestionSet(QUESTIONNAIRE_SET);
    questionnaire.setDispatchDateTime(QUESTIONNAIRE_DISPATCHDATE_TIMESTAMP);
    questionnaire.setReceiptDateTime(QUESTIONNAIRE_RECEIPTDATE_TIMESTAMP);
    questionnaire.setResponseDateTime(QUESTIONNAIRE_RESPONSEDATE_TIMESTAMP);
    return questionnaire;
  }

  /**
   * builder
   * @return the questionnaire built
   */
  public static QuestionnaireBuilder questionnaire() {
    return new QuestionnaireBuilder();
  }
}
