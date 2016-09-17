package uk.gov.ons.ctp.response.action.export.templating;

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
import uk.gov.ons.ctp.response.action.export.templating.freemarker.service.FreeMarkerService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;
import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

/**
 * This test focuses on the FreeMarker templating. It first stores a template in the MongoDB and then it uses the
 * TransformationService to verify that a list of ActionRequests can be filed or streamed using the template.
 *
 * Prerequisites:
 *    - a running MongoDB database 'actionExport' (see application.yml and FreeMarkerITCaseConfig)
 */
@Slf4j
@SpringBootTest(classes = {FreeMarkerITCaseConfig.class})
@RunWith(SpringRunner.class)
public class FreeMarkerITCase {

  private static final int TEST_STRING_LENGTH = 3447;
  private static final String TEST_FILE_PATH = "/tmp/csv/forPrinter.csv";
  private static final String FREEMARKER_TEMPLATE_NAME = "curltest";

  @Autowired
  FreeMarkerService freeMarkerService;

  @Autowired
  TransformationService transformationService;

  @Before
  public void setup() throws CTPException {
    log.debug("About to store the FreeMarker template...");
    freeMarkerService.storeTemplate(FREEMARKER_TEMPLATE_NAME, getClass().getResourceAsStream("/templates/freemarker/curltest_validtemplate.ftl"));
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
    assertEquals(result.length(), TEST_STRING_LENGTH);
  }

  @Test
  public void testStreamMePositiveScenario() throws CTPException, UnsupportedEncodingException {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    ByteArrayOutputStream result = transformationService.streamMe(actionRequestDocumentList, FREEMARKER_TEMPLATE_NAME);
    assertNotNull(result);
    String resultString = result.toString(UTF8.name());
    assertEquals(resultString.length(), TEST_STRING_LENGTH);

  }

  private static List<ActionRequestDocument> buildMeListOfActionRequestDocuments() {
    List<ActionRequestDocument> result = new ArrayList<>();
    for (int i = 1; i < 51; i++) {
      result.add(buildAMeActionRequestDocument(i));
    }
    return result;
  }

  private static ActionRequestDocument buildAMeActionRequestDocument(int i) {
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
