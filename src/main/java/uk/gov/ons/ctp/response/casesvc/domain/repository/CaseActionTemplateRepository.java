package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate.Handler;

@Repository
@Transactional(readOnly = true)
public interface CaseActionTemplateRepository extends JpaRepository<CaseActionTemplate, Integer> {

  CaseActionTemplate findByTagAndHandler(String tag, Handler handler);

  CaseActionTemplate findByType(String type);
}
