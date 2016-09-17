package uk.gov.ons.ctp.response.action.export.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.response.action.export.GenericTestConfig;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
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

@RunWith(SpringRunner.class)
@SpringBootTest
public class TransformationServiceImplITCase {

  private static final int TEST_STRING_LENGTH = 3501;
  private static final String TEST_FILE_PATH = "/tmp/csv/forPrinter.csv";
  private static final String CSV_EXPORT_TEMPLATE = "csvExport.ftl";

  @Autowired
  TransformationService transformationService;

  @Test
  public void testFileMePositiveScenario() {
    // Delete the file if present
    File forPrinterFile = new File(TEST_FILE_PATH);
    if (forPrinterFile != null && forPrinterFile.exists()) {
      forPrinterFile.delete();
    }

    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    File result = transformationService.fileMe(actionRequestDocumentList, CSV_EXPORT_TEMPLATE, TEST_FILE_PATH);
    assertNotNull(result);
    assertEquals(result.length(), TEST_STRING_LENGTH);
  }

  @Test
  public void testStreamMePositiveScenario() throws UnsupportedEncodingException {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    ByteArrayOutputStream result = transformationService.streamMe(actionRequestDocumentList, CSV_EXPORT_TEMPLATE);
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
