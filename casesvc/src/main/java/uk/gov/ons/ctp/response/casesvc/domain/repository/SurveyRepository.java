package uk.gov.ons.ctp.response.casesvc.domain.repository;

import javax.inject.Named;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Survey;

/**
 * JPA Data Repository
 */
@Named
public interface SurveyRepository extends JpaRepository<Survey, Integer> {

}
