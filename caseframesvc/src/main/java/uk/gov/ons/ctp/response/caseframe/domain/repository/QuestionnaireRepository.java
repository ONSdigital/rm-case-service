package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Named;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;

/**
 * JPA Data Repository for Questionnaire entities.
 * Note - CRUD methods on repository instances are transactional by default
 */
@Named
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Integer> {

  Questionnaire findByIac(String iac);

  List<Questionnaire> findByCaseId(Integer caseId);

  Questionnaire findByQuestionnaireId(Integer qid);

  /**
   * set the response datetime for a given questionnaire.
   * @param responseDatetime the time to set
   * @param questionnaireid identity of the questionnaire to update
   * @return  the number of questionnaires updated
   */
  @Modifying
  @Query(value = "UPDATE caseframe.questionnaire SET response_datetime = ?1 WHERE questionnaireid = ?2", nativeQuery = true)
  int setResponseDatetimeFor(Timestamp responseDatetime, Integer questionnaireid);

}
