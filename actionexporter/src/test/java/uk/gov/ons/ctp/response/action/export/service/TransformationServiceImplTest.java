package uk.gov.ons.ctp.response.action.export.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;
import uk.gov.ons.ctp.response.action.export.repository.ActionRequestRepository;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;

import java.util.ArrayList;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.mockito.Mockito.when;
import static uk.gov.ons.ctp.response.action.export.utility.ObjectBuilder.buildListOfActionRequestDocuments;

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
  public void testProcessActionRequestsNothingToProcess() {
    when(actionRequestRepo.findByDateSentIsNullOrderByActionTypeDesc()).thenReturn(new ArrayList<>());
    SftpMessage sftpMessage = transformationService.processActionRequests();
    assertNotNull(sftpMessage);
    assertTrue(sftpMessage.getOutputStreams().isEmpty());
    assertTrue(sftpMessage.getActionRequestIds().isEmpty());
  }

  @Test
  public void testProcessActionRequests() {
    when(actionRequestRepo.findByDateSentIsNullOrderByActionTypeDesc()).thenReturn(buildListOfActionRequestDocuments());
    // TODO
    //SftpMessage sftpMessage = transformationService.processActionRequests();
    //assertNotNull(sftpMessage);
  }

}
