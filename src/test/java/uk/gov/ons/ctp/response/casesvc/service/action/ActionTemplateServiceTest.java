package uk.gov.ons.ctp.response.casesvc.service.action;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionTemplate;
import uk.gov.ons.ctp.response.casesvc.domain.repository.CaseActionTemplateRepository;
import uk.gov.ons.ctp.response.casesvc.representation.action.ActionTemplateDTO;

@RunWith(MockitoJUnitRunner.class)
public class ActionTemplateServiceTest {
  @Mock private CaseActionTemplateRepository actionTemplateRepository;
  @InjectMocks private ActionTemplateService actionTemplateService;

  private CaseActionTemplate mockMpsActionTemplate = new CaseActionTemplate();
  private CaseActionTemplate mockReminderActionTemplate = new CaseActionTemplate();
  private CaseActionTemplate mockGoLiveActionTemplate = new CaseActionTemplate();

  @Before
  public void setUp() throws Exception {
    mockMpsActionTemplate.setType("BSNL");
    mockMpsActionTemplate.setDescription("Business Survey Notification Letter");
    mockMpsActionTemplate.setPrefix("BSNOT");
    mockMpsActionTemplate.setTag("mps");
    mockMpsActionTemplate.setHandler(ActionTemplateDTO.Handler.LETTER);
    Mockito.when(
            actionTemplateRepository.findByTagAndHandler("mps", ActionTemplateDTO.Handler.LETTER))
        .thenReturn(mockMpsActionTemplate);

    mockReminderActionTemplate.setType("BSRE");
    mockReminderActionTemplate.setDescription("Business Survey Reminder Email");
    mockReminderActionTemplate.setPrefix(null);
    mockReminderActionTemplate.setTag("reminder");
    mockReminderActionTemplate.setHandler(ActionTemplateDTO.Handler.EMAIL);
    Mockito.when(
            actionTemplateRepository.findByTagAndHandler(
                "reminder", ActionTemplateDTO.Handler.EMAIL))
        .thenReturn(mockReminderActionTemplate);

    mockGoLiveActionTemplate.setType("BSNE");
    mockGoLiveActionTemplate.setDescription("Business Survey Notification Email");
    mockGoLiveActionTemplate.setPrefix(null);
    mockGoLiveActionTemplate.setTag("go_live");
    mockGoLiveActionTemplate.setHandler(ActionTemplateDTO.Handler.LETTER);
    Mockito.when(
            actionTemplateRepository.findByTagAndHandler(
                "go_live", ActionTemplateDTO.Handler.LETTER))
        .thenReturn(mockGoLiveActionTemplate);
  }

  @Test
  public void testMapEventTagToTemplateReturnsCorrectTemplate() {
    Assert.assertEquals(
        mockMpsActionTemplate, actionTemplateService.mapEventTagToTemplate("mps", false));
  }

  @Test
  public void testMapEventTagToTemplateReturnsDifferentResult() {
    CaseActionTemplate actualActionTemplate =
        actionTemplateService.mapEventTagToTemplate("nudge_email_0", true);
    Assert.assertNotEquals(mockMpsActionTemplate, actualActionTemplate);
    Assert.assertNull(actualActionTemplate);
  }

  @Test
  public void testMapEventTagToTemplateReturnsAssociatedTemplates() {
    CaseActionTemplate actualReminderActionTemplate =
        actionTemplateService.mapEventTagToTemplate("reminder2", true);
    Assert.assertEquals(mockReminderActionTemplate, actualReminderActionTemplate);
    Assert.assertNotNull(actualReminderActionTemplate);
    CaseActionTemplate actualGoLiveActionTemplate =
        actionTemplateService.mapEventTagToTemplate("go_live", false);
    Assert.assertEquals(mockGoLiveActionTemplate, actualGoLiveActionTemplate);
    Assert.assertNotNull(actualGoLiveActionTemplate);
    CaseActionTemplate actualMPSActionTemplate =
        actionTemplateService.mapEventTagToTemplate("mps", false);
    Assert.assertEquals(mockMpsActionTemplate, actualMPSActionTemplate);
    Assert.assertNotNull(actualMPSActionTemplate);
  }
}
