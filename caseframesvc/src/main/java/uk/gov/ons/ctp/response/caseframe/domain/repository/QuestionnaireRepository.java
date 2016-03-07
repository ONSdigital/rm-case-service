package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.sql.Timestamp;
import java.util.List;

import javax.inject.Named;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;

/**
 * JPA Data Repository for Questionnaire entities. Note - CRUD methods on
 * repository instances are transactional by default
 */
@Named
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Integer> {

  /**
   * find Questionnaire by Internet Access Code
   *
   * @param iac the IAC to find by
   * @return the Questionnaire or null if not found
   */
  Questionnaire findByIac(String iac);

  /**
   * find questionnaire by case id
   *
   * @param caseId the id to find by
   * @return the Questionnaire or null if not found
   */
  List<Questionnaire> findByCaseId(Integer caseId);

  /**
   * find questionnnaire by id
   *
   * @param qid the questionnaire id to find by
   * @return the Questionnaire or null if not found
   */
  Questionnaire findByQuestionnaireId(Integer qid);

  /**
   * set the response datetime for a given questionnaire.
   *
   * @param responseDatetime the time to set
   * @param questionnaireid identity of the questionnaire to update
   * @return the number of questionnaires updated
   */
  @Modifying
  @Query(value = "UPDATE caseframe.questionnaire SET response_datetime = ?1 WHERE questionnaireid = ?2",
         nativeQuery = true)
  int setResponseDatetimeFor(Timestamp responseDatetime, Integer questionnaireid);

}
