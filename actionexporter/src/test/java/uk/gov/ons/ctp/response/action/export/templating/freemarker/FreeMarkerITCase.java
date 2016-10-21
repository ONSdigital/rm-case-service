package uk.gov.ons.ctp.response.action.export.templating.freemarker;

import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.export.service.DocumentService;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;
import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;
import static uk.gov.ons.ctp.response.action.export.service.TransformationServiceImplTest.buildMeListOfActionRequestDocuments;

/**
 * This test focuses on the FreeMarker templating. It first stores a template in the MongoDB and then it uses the
 * TransformationService to verify that a list of ActionRequests can be filed or streamed using the template.
 *
 * Prerequisites:
 *    - a running MongoDB database (see application-test.properties for config)
 */
@Slf4j
@SpringBootTest(classes = {FreeMarkerITCaseConfig.class})
@RunWith(SpringRunner.class)
public class FreeMarkerITCase {

  private static final int TEST_STRING_LENGTH_WHEN_50_ACTION_REQUESTS = 3501;
  private static final int TEST_STRING_LENGTH_WHEN_EMPTY_ACTION_REQUESTS =160;
  private static final String TEST_FILE_PATH = "/tmp/ctp/forPrinter.csv";
  private static final String FREEMARKER_TEMPLATE_NAME = "curltest";
  private static final String FREEMARKER_TEMPLATE_NON_EXISTING_NAME = "totalRandom";

  @Autowired
  DocumentService documentService;

  @Autowired
  TransformationService transformationService;

  @Before
  public void setup() throws CTPException {
    log.debug("About to store the FreeMarker template...");
    documentService.storeContentDocument(FREEMARKER_TEMPLATE_NAME, getClass().getResourceAsStream("/templates/freemarker/curltest_validtemplate.ftl"));
    log.debug("FreeMarker template stored successfully...");
  }

  @Test
  public void testFileMePositiveScenario() throws CTPException {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    File result = transformationService.fileMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME, TEST_FILE_PATH);
    assertNotNull(result);
    assertEquals(result.length(), TEST_STRING_LENGTH_WHEN_50_ACTION_REQUESTS);
  }

  @Test
  public void testFileMeScenarioMissingTemplate() {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    boolean exceptionThrown = false;
    try {
      transformationService.fileMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NON_EXISTING_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFileMeScenarioNullActionRequests() {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = null;
    boolean exceptionThrown = false;
    try {
      transformationService.fileMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME, TEST_FILE_PATH);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testFileMeScenarioEmptyActionRequests() throws CTPException {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = new ArrayList<>();
    File result = transformationService.fileMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME, TEST_FILE_PATH);
    assertNotNull(result);
    assertEquals(result.length(), TEST_STRING_LENGTH_WHEN_EMPTY_ACTION_REQUESTS);
  }

  @Test
  public void testStreamMePositiveScenario() throws CTPException, UnsupportedEncodingException {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    ByteArrayOutputStream result = transformationService.streamMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    assertNotNull(result);
    String resultString = result.toString(UTF8.name());
    assertEquals(resultString.length(), TEST_STRING_LENGTH_WHEN_50_ACTION_REQUESTS);

  }


  @Test
  public void testStreamMeScenarioMissingTemplate() {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    boolean exceptionThrown = false;
    try {
      transformationService.streamMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NON_EXISTING_NAME);
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
      transformationService.streamMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
    }
    assertTrue(exceptionThrown);
  }

  @Test
  public void testStreamMeScenarioEmptyActionRequests() throws CTPException, UnsupportedEncodingException {
    List<ActionRequestDocument> actionRequestDocumentList = new ArrayList<>();
    ByteArrayOutputStream result = transformationService.streamMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    assertNotNull(result);
    String resultString = result.toString(UTF8.name());
    assertEquals(resultString.length(), TEST_STRING_LENGTH_WHEN_EMPTY_ACTION_REQUESTS);
  }
}
