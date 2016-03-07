package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.QuestionSet;

/**
 * JPA Data Repository.
 */
@Repository
public interface QuestionSetRepository extends JpaRepository<QuestionSet, Integer> {

  QuestionSet findByQuestionSet(String questionSet);

}
