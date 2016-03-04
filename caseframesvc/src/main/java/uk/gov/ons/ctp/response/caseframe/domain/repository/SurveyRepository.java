package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.inject.Named;

import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;

/**
 * JPA Data Repository
 */
@Named
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

}
