package uk.gov.ons.ctp.response.caseframe.service.impl;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.repository.CaseRepository;
import uk.gov.ons.ctp.response.caseframe.domain.repository.QuestionnaireRepository;
import uk.gov.ons.ctp.response.caseframe.service.QuestionnaireService;

/**
 * An implementation of the QuestionnaireService using JPA Repository class(es)
 * The business logic for the application should reside here.
 *
 */
@Named
@Slf4j
public class QuestionnaireServiceImpl implements QuestionnaireService {

  public static final String CLOSED = "CLOSED";

  @Inject
  private CaseRepository caseRepo;

  @Inject
  private QuestionnaireRepository questionnaireRepo;

  @Override
  public Questionnaire findQuestionnaireByIac(String iac) {
    log.debug("Entering findQuestionnaireByIac with {}", iac);
    return questionnaireRepo.findByIac(iac);
  }

  @Override
  public List<Questionnaire> findQuestionnairesByCaseId(Integer caseId) {
    log.debug("Entering findQuestionnairesByCaseId with {}", caseId);
    return questionnaireRepo.findByCaseId(caseId);
  }

  @Override
  public int updateResponseTime(Integer questionnaireid) {
    Timestamp currentTime = new Timestamp(System.currentTimeMillis());
    return questionnaireRepo.setResponseDatetimeFor(currentTime, questionnaireid);
  }

  @Override
  public int closeParentCase(Integer questionnaireid) {
    Questionnaire questionnaire = questionnaireRepo.findOne(questionnaireid);
    return caseRepo.setStatusFor(CLOSED, questionnaire.getCaseId());
  }
}