package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO.Handler;

@Repository
@Transactional(readOnly = true)
public interface ActionTemplateRepository extends JpaRepository<ActionTemplate, Integer> {

    ActionTemplate findByTagAndHandler(String tag, Handler handler);
}
