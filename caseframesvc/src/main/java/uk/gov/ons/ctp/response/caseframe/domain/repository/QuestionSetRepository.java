package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.QuestionSet;

/**
 * JPA Data Repository.
 */
@Repository
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Integer> {

  /**
   * find question by question set
   * @param questionSet the question name set to find by
   * @return the QuestionSet or null if not found
   */
  QuestionSet findByQuestionSet(String questionSet);

}
