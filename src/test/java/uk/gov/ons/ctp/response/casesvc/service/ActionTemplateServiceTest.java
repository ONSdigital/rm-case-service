package uk.gov.ons.ctp.response.casesvc.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ActionTemplateRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ActionTemplateDTO;
import uk.gov.ons.ctp.response.lib.common.FixtureHelper;

import static junit.framework.Assert.assertNotSame;
import static junit.framework.TestCase.*;

@RunWith(MockitoJUnitRunner.class)
public class ActionTemplateServiceTest {
    @Mock private ActionTemplateRepository actionTemplateRepository;
    @InjectMocks
    private ActionTemplateService actionTemplateService;

    private ActionTemplate mockMpsActionTemplate = new ActionTemplate();
    private ActionTemplate mockReminderActionTemplate = new ActionTemplate();
    private ActionTemplate mockGoLiveActionTemplate = new ActionTemplate();

    @Before
    public void setUp() throws Exception {
        mockMpsActionTemplate.setName("BSNL");
        mockMpsActionTemplate.setDescription("Business Survey Notification Letter");
        mockMpsActionTemplate.setPrefix("BSNOT");
        mockMpsActionTemplate.setTag("mps");
        mockMpsActionTemplate.setHandler(ActionTemplateDTO.Handler.LETTER);
        Mockito.when(actionTemplateRepository.findByTagAndHandler("mps", ActionTemplateDTO.Handler.LETTER)).
                thenReturn(mockMpsActionTemplate);

        mockReminderActionTemplate.setName("BSRE");
        mockReminderActionTemplate.setDescription("Business Survey Reminder Email");
        mockReminderActionTemplate.setPrefix(null);
        mockReminderActionTemplate.setTag("reminder");
        mockReminderActionTemplate.setHandler(ActionTemplateDTO.Handler.EMAIL);
        Mockito.when(actionTemplateRepository.findByTagAndHandler("reminder", ActionTemplateDTO.Handler.EMAIL)).
                thenReturn(mockReminderActionTemplate);

        mockGoLiveActionTemplate.setName("BSNE");
        mockGoLiveActionTemplate.setDescription("Business Survey Notification Email");
        mockGoLiveActionTemplate.setPrefix(null);
        mockGoLiveActionTemplate.setTag("go_live");
        mockGoLiveActionTemplate.setHandler(ActionTemplateDTO.Handler.LETTER);
        Mockito.when(actionTemplateRepository.findByTagAndHandler("go_live", ActionTemplateDTO.Handler.LETTER)).
                thenReturn(mockGoLiveActionTemplate);
    }

    @Test
    public void testMapEventTagToTemplateReturnsCorrectTemplate() {
        assertEquals(mockMpsActionTemplate, actionTemplateService.mapEventTagToTemplate("mps", false));
    }


    @Test
    public void testMapEventTagToTemplateReturnsDifferentResult() {
        ActionTemplate actualActionTemplate = actionTemplateService.mapEventTagToTemplate("nudge_email_0",
                true);
        assertNotSame(mockMpsActionTemplate, actualActionTemplate);
        assertNull(actualActionTemplate);
    }

    @Test
    public void testMapEventTagToTemplateReturnsAssociatedTemplates() {
        ActionTemplate actualReminderActionTemplate = actionTemplateService.mapEventTagToTemplate("reminder2",
                true);
        assertEquals(mockReminderActionTemplate, actualReminderActionTemplate);
        assertNotNull(actualReminderActionTemplate);
        ActionTemplate actualGoLiveActionTemplate = actionTemplateService.mapEventTagToTemplate("go_live",
                false);
        assertEquals(mockGoLiveActionTemplate, actualGoLiveActionTemplate);
        assertNotNull(actualGoLiveActionTemplate);
        ActionTemplate actualMPSActionTemplate = actionTemplateService.mapEventTagToTemplate("mps",
                false);
        assertEquals(mockMpsActionTemplate, actualMPSActionTemplate);
        assertNotNull(actualMPSActionTemplate);
    }

}
