package uk.gov.ons.ctp.response.casesvc.service;

import com.godaddy.logging.Logger;
import com.godaddy.logging.LoggerFactory;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO.Handler;

@Service
public class ActionTemplateService {
    private static final Logger log = LoggerFactory.getLogger(ActionTemplateService.class);
    private ActionTemplateRepository actionTemplateRepository;

    public ActionTemplateService(ActionTemplateRepository actionTemplateRepository) {
        this.actionTemplateRepository = actionTemplateRepository;
    }

    /**
     * Maps event tag and active enrolment to an action template
     * @param tag Event tag provided as a part of the endpoint
     * @param isActiveEnrolment derived active enrolment from the case
     * @return ActionTemplate action template which will contain the template name
     */
    public ActionTemplate mapEventTagToTemplate(final String tag, final boolean isActiveEnrolment) {
        String eventTag = TemplateMapper.valueOf(tag).getEventTagMapping();
        Handler handler = isActiveEnrolment ? Handler.EMAIL : Handler.LETTER;
        ActionTemplate template= actionTemplateRepository.findByTagAndHandler(eventTag, handler);
        if (template == null) {
            log.with("tag", tag).with("active enrolment", isActiveEnrolment).
                    warn("Could not map evnet tag to template");
        }
        log.with("tag", tag).with("active enrolment", isActiveEnrolment).
                debug("Template Found");
        return template;
    }

    /**
     *  Event Tag to ActionTemplate mapper
     */
    private enum TemplateMapper {
        mps("mps"),
        go_live("go_live"),
        reminder("reminder"),
        reminder2("reminder"),
        reminder3("reminder"),
        nudge_email_0("nudge"),
        nudge_email_1("nudge"),
        nudge_email_2("nudge"),
        nudge_email_3("nudge"),
        nudge_email_4("nudge");

        TemplateMapper(final String tag) {
            this.mappedTag = tag;
        }

        public String getEventTagMapping() {
            return mappedTag;
        }

        private String mappedTag;
    }
}
