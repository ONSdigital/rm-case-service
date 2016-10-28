package uk.gov.ons.ctp.response.action.export.service;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;
import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static uk.gov.ons.ctp.response.action.export.utility.ObjectBuilder.buildListOfActionRequestDocuments;

/**
 * This test focuses on the FreeMarker templating. It first stores a template in the MongoDB and then it uses the
 * TransformationService to verify that a list of ActionRequests can be filed or streamed using the template.
 *
 * Prerequisites:
 *    - a running MongoDB database (see application-test.properties for config)
 */
@Slf4j
@SpringBootTest(classes = {TemplateServiceImplITCaseConfig.class})
@RunWith(SpringRunner.class)
public class TemplateServiceImplITCase {

  private static final int TEST_STRING_LENGTH_WHEN_50_ACTION_REQUESTS = 2614;
  private static final int TEST_STRING_LENGTH_WHEN_EMPTY_ACTION_REQUESTS =114;
  private static final String TEST_FILE_PATH = "/tmp/ctp/forPrinter.csv";
  private static final String FREEMARKER_TEMPLATE_NAME = "curltest";
  private static final String FREEMARKER_TEMPLATE_NON_EXISTING_NAME = "totalRandom";

  @Autowired
  TemplateService templateService;

  @Before
  public void setup() throws CTPException {
    log.debug("About to store the FreeMarker template...");
    templateService.storeTemplateDocument(FREEMARKER_TEMPLATE_NAME, getClass().getResourceAsStream("/templates/freemarker/valid_template.ftl"));
    log.debug("FreeMarker template stored successfully...");
  }

  @Test
  public void testFilePositiveScenario() throws CTPException {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = buildListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    File result = templateService.file(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME, TEST_FILE_PATH);
    assertNotNull(result);
    assertEquals(result.length(), TEST_STRING_LENGTH_WHEN_50_ACTION_REQUESTS);
  }

  @Test
  public void testFileScenarioMissingTemplate() {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = buildListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    boolean exceptionThrown = false;
    try {
      templateService.file(actionRequestDocumentList, FREEMARKER_TEMPLATE_NON_EXISTING_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFileScenarioNullActionRequests() {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = null;
    boolean exceptionThrown = false;
    try {
      templateService.file(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFileScenarioEmptyActionRequests() throws CTPException {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = new ArrayList<>();
    File result = templateService.file(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME, TEST_FILE_PATH);
    assertNotNull(result);
    assertEquals(result.length(), TEST_STRING_LENGTH_WHEN_EMPTY_ACTION_REQUESTS);
  }

  @Test
  public void testStreamPositiveScenario() throws CTPException, UnsupportedEncodingException {
    List<ActionRequestDocument> actionRequestDocumentList = buildListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    ByteArrayOutputStream result = templateService.stream(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    assertNotNull(result);
    String resultString = result.toString(UTF8.name());
    assertEquals(resultString.length(), TEST_STRING_LENGTH_WHEN_50_ACTION_REQUESTS);

  }


  @Test
  public void testStreamScenarioMissingTemplate() {
    List<ActionRequestDocument> actionRequestDocumentList = buildListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    boolean exceptionThrown = false;
    try {
      templateService.stream(actionRequestDocumentList, FREEMARKER_TEMPLATE_NON_EXISTING_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }


  @Test
  public void testStreameMeScenarioNullActionRequests() {
    List<ActionRequestDocument> actionRequestDocumentList = null;
    boolean exceptionThrown = false;
    try {
      templateService.stream(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testStreamScenarioEmptyActionRequests() throws CTPException, UnsupportedEncodingException {
    List<ActionRequestDocument> actionRequestDocumentList = new ArrayList<>();
    ByteArrayOutputStream result = templateService.stream(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    assertNotNull(result);
    String resultString = result.toString(UTF8.name());
    assertEquals(resultString.length(), TEST_STRING_LENGTH_WHEN_EMPTY_ACTION_REQUESTS);
  }
}
