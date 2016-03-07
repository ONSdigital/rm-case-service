package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.QuestionSet;

/**
 * The QuestionSet Service interface defines all business behaviours for
 * operations on the QuestionSet entity model.
 */
public interface QuestionSetService extends CTPService {

  /**
   * Return all QuestionSet entities.
   *
   * @return List of QuestionSet entities or empty List
   */
  List<QuestionSet> findQuestionSets();

  /**
   * Find QuestionSet entity by QuestionSet code String.
   *
   * @param questionSet Unique QuestionSet name String
   * @return QuestionSet object or null
   */
  QuestionSet findQuestionSetByQuestionSet(String questionSet);
}
