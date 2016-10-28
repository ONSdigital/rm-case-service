package uk.gov.ons.ctp.response.action.export.service;

import freemarker.template.Template;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import org.testng.Assert;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.repository.TemplateRepository;
import uk.gov.ons.ctp.response.action.export.service.impl.TemplateServiceImpl;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertNotNull;
import static uk.gov.ons.ctp.response.action.export.service.impl.TemplateServiceImpl.ERROR_RETRIEVING_FREEMARKER_TEMPLATE;
import static uk.gov.ons.ctp.response.action.export.service.impl.TemplateServiceImpl.EXCEPTION_STORE_TEMPLATE;
import static uk.gov.ons.ctp.response.action.export.utility.ObjectBuilder.buildListOfActionRequestDocuments;

/**
 * To unit test TemplateServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateServiceImplTest {
  private static final String TEMPLATE_NAME = "testTemplate";
  private static final String TEST_FILE_PATH = "/tmp/ctp/forPrinter.csv";

  @InjectMocks
  TemplateServiceImpl templateService;

  @Mock
  TemplateRepository repository;

  @Mock
  freemarker.template.Configuration configuration;

  @Test
  public void testStoreNullTemplate() {
    boolean exceptionThrown = false;
    try {
      templateService.storeTemplateDocument(TEMPLATE_NAME, null);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_STORE_TEMPLATE, e.getMessage());
    }
    assertTrue(exceptionThrown);
    verify(repository, times(0)).save(any(TemplateDocument.class));
    verify(configuration, times(0)).clearTemplateCache();
  }

  @Test
  public void testStoreEmptyTemplate() {
    boolean exceptionThrown = false;
    try {
      templateService.storeTemplateDocument(TEMPLATE_NAME,getClass().getResourceAsStream("/templates/freemarker/empty_template.ftl"));
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_STORE_TEMPLATE, e.getMessage());
    }
    assertTrue(exceptionThrown);
    verify(repository, times(0)).save(any(TemplateDocument.class));
    verify(configuration, times(0)).clearTemplateCache();
  }

  @Test
  public void testStoreValidTemplate() throws CTPException {
    templateService.storeTemplateDocument(TEMPLATE_NAME, getClass().getResourceAsStream("/templates/freemarker/valid_template.ftl"));
    verify(repository, times(1)).save(any(TemplateDocument.class));
    verify(configuration, times(1)).clearTemplateCache();
  }

  @Test
  public void testFileIssueRetrievingTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenThrow(new IOException());
    boolean exceptionThrown = false;
    try {
      templateService.file(buildListOfActionRequestDocuments(), TEMPLATE_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      Assert.assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    TestCase.assertTrue(exceptionThrown);
  }

  @Test
  public void testFileNullRetrievedTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(null);
    boolean exceptionThrown = false;
    try {
      templateService.file(buildListOfActionRequestDocuments(), TEMPLATE_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      Assert.assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ERROR_RETRIEVING_FREEMARKER_TEMPLATE, e.getMessage());
    }
    TestCase.assertTrue(exceptionThrown);
  }

  @Test
  public void testFile() throws CTPException, IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(Mockito.mock(Template.class));
    File result = templateService.file(buildListOfActionRequestDocuments(), TEMPLATE_NAME, TEST_FILE_PATH);
    assertNotNull(result);
  }

  @Test
  public void testStreamIssueRetrievingTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenThrow(new IOException());
    boolean exceptionThrown = false;
    try {
      templateService.stream(buildListOfActionRequestDocuments(), TEMPLATE_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      Assert.assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    TestCase.assertTrue(exceptionThrown);
  }

  @Test
  public void testStreamNullRetrievedTemplate() throws IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(null);
    boolean exceptionThrown = false;
    try {
      templateService.stream(buildListOfActionRequestDocuments(), TEMPLATE_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      Assert.assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(ERROR_RETRIEVING_FREEMARKER_TEMPLATE, e.getMessage());
    }
    TestCase.assertTrue(exceptionThrown);
  }

  @Test
  public void testStream() throws CTPException, IOException {
    Mockito.when(configuration.getTemplate(TEMPLATE_NAME)).thenReturn(Mockito.mock(Template.class));
    ByteArrayOutputStream result = templateService.stream(buildListOfActionRequestDocuments(), TEMPLATE_NAME);
    assertNotNull(result);
  }
}
