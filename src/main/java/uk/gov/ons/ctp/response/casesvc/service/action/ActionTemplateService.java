package uk.gov.ons.ctp.response.casesvc.service.action;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate.Handler;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionTemplateRepository;

@Service
public class ActionTemplateService {
  private static final Logger log = LoggerFactory.getLogger(ActionTemplateService.class);
  private CaseActionTemplateRepository actionTemplateRepository;

  public ActionTemplateService(CaseActionTemplateRepository actionTemplateRepository) {
    this.actionTemplateRepository = actionTemplateRepository;
  }

  /**
   * Maps event tag and active enrolment to an action template
   *
   * @param tag Event tag provided as a part of the endpoint
   * @param isActiveEnrolment derived active enrolment from the case
   * @return ActionTemplate action template which will contain the template name
   */
  public CaseActionTemplate mapEventTagToTemplate(
      final String tag, final boolean isActiveEnrolment) {
    Handler handler = isActiveEnrolment ? Handler.EMAIL : Handler.LETTER;
    CaseActionTemplate template = actionTemplateRepository.findByTagAndHandler(tag, handler);
    if (template == null) {
      log, kv("tag", tag)
          , kv("activeEnrolment", isActiveEnrolment)
          .warn("No Template registered against the event and active enrolment");
    }
    log, kv("tag", tag), kv("activeEnrolment", isActiveEnrolment).debug("Template Found");
    return template;
  }
}
