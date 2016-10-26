package uk.gov.ons.ctp.response.action.export.service;

import freemarker.template.Template;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotNull;
import static uk.gov.ons.ctp.response.action.export.service.impl.TransformationServiceImpl.ERROR_RETRIEVING_FREEMARKER_TEMPLATE;

/**
 * To unit test TransformationServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class TransformationServiceImplTest {
  @InjectMocks
  TransformationServiceImpl transformationService;

  @Mock
  freemarker.template.Configuration configuration;

  private static final String TEMPLATE_NAME = "testTemplate";
  private static final String TEST_FILE_PATH = "/tmp/ctp/forPrinter.csv";

  @Test
  public void testFileIssueRetrievingTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenThrow(new IOException());
    boolean exceptionThrown = false;
    try {
      transformationService.file(buildListOfActionRequestDocuments(), TEMPLATE_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFileNullRetrievedTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(null);
    boolean exceptionThrown = false;
    try {
      transformationService.file(buildListOfActionRequestDocuments(), TEMPLATE_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ERROR_RETRIEVING_FREEMARKER_TEMPLATE, e.getMessage());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFile() throws CTPException, IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(Mockito.mock(Template.class));
    File result = transformationService.file(buildListOfActionRequestDocuments(), TEMPLATE_NAME, TEST_FILE_PATH);
    assertNotNull(result);
  }

  @Test
  public void testStreamIssueRetrievingTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenThrow(new IOException());
    boolean exceptionThrown = false;
    try {
      transformationService.stream(buildListOfActionRequestDocuments(), TEMPLATE_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testStreamNullRetrievedTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(null);
    boolean exceptionThrown = false;
    try {
      transformationService.stream(buildListOfActionRequestDocuments(), TEMPLATE_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ERROR_RETRIEVING_FREEMARKER_TEMPLATE, e.getMessage());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testStream() throws CTPException, IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(Mockito.mock(Template.class));
    ByteArrayOutputStream result = transformationService.stream(buildListOfActionRequestDocuments(), TEMPLATE_NAME);
    assertNotNull(result);
  }

  public static List<ActionRequestDocument> buildListOfActionRequestDocuments() {
    List<ActionRequestDocument> result = new ArrayList<>();
    for (int i = 1; i < 51; i++) {
      result.add(buildActionRequestDocument(i));
    }
    return result;
  }

  private static ActionRequestDocument buildActionRequestDocument(int i) {
    ActionRequestDocument result =  new ActionRequestDocument();
    result.setActionId(new BigInteger(new Integer(i).toString()));
    result.setActionType("testActionType");
    result.setIac("testIac");
    result.setAddress(buildActionAddress());
    return result;
  }

  private static ActionAddress buildActionAddress() {
    ActionAddress actionAddress = new ActionAddress();
    actionAddress.setLine1("1 High Street");
    actionAddress.setTownName("Southampton");
    actionAddress.setPostcode("SO16 0AS");
    return actionAddress;
  }
}
