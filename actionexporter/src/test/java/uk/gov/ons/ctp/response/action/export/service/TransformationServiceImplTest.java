package uk.gov.ons.ctp.response.action.export.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;

/**
 * To unit test TransformationServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class TransformationServiceImplTest {
  @InjectMocks
  TransformationServiceImpl transformationService;

  @Mock
  private ActionRequestRepository actionRequestRepo;

  @Mock
  private TemplateService templateService;

  @Mock
  private TemplateMappingService templateMappingService;

  @Test
  public void testProcessActionRequests() {
    // TODO
  }

}
