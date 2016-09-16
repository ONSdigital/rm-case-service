package uk.gov.ons.ctp.response.action.export.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.response.action.export.GenericTestConfig;
import uk.gov.ons.ctp.response.action.export.config.FreeMarkerConfiguration;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import java.io.File;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;
import static org.testng.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GenericTestConfig.class, FreeMarkerConfiguration.class})
public class TransformationServiceImplITCase {

  @Autowired
  TransformationService transformationService;

  @Test
  public void testFileMePositiveScenario() {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    File result = transformationService.fileMe(actionRequestDocumentList, "/tmp/csv/forPrinter.csv");
    assertNotNull(result);
    // TODO assert the file is not there initially in a @Before and then that it has been created
  }

  @Test
  public void testStreamMePositiveScenario() {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    OutputStream result= transformationService.streamMe(actionRequestDocumentList);
    assertNotNull(result);
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
