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

    public ActionTemplate mapEventTagToTemplate(final String tag, final boolean activeEnrolment) {
        String eventTag = TemplateMapper.valueOf(tag).getEventTagMapping();
        Handler handler = getHandler(activeEnrolment);
        ActionTemplate template= actionTemplateRepository.findByTagAndHandler(eventTag, handler);
        if (template == null) {
            log.with("tag", tag).with("active enrolment", activeEnrolment).
                    warn("Could not map evnet tag to template");
        }

        return template;
    }

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
    private Handler getHandler(boolean isActiveEnrolment) {
        return isActiveEnrolment ? Handler.EMAIL : Handler.LETTER;
    }

}
