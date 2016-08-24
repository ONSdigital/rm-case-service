package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.QuestionSet;
import uk.gov.ons.ctp.response.casesvc.domain.repository.QuestionSetRepository;
import uk.gov.ons.ctp.response.casesvc.service.QuestionSetService;

/**
 * An implementation of the QuestionSetService using JPA Repository class(es)
 * The business logic for the application should reside here.
 */
@Named
@Slf4j
public class QuestionSetServiceImpl implements QuestionSetService {

  @Inject
  private QuestionSetRepository questionSetRepo;

  @Override
  public List<QuestionSet> findQuestionSets() {
    log.debug("Entering findQuestionSets");
    return questionSetRepo.findAll();
  }

  @Override
  public QuestionSet findQuestionSetByQuestionSet(final String questionSet) {
    log.debug("Entering findQuestionSetByQuestionSet with {}", questionSet);
    return questionSetRepo.findByQuestionSet(questionSet);
  }

}
