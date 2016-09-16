package uk.gov.ons.ctp.response.action.export.service.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import uk.gov.ons.ctp.response.action.export.GenericTestConfig;
import uk.gov.ons.ctp.response.action.export.config.FreeMarkerConfiguration;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.FileService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.testng.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = {GenericTestConfig.class, FreeMarkerConfiguration.class})
public class FileServiceImplITCase {

  @Autowired
  FileService fileService;

  @Test
  public void testPositiveScenario() {
    List<ActionRequestDocument> actionRequestDocumentList = buildMeListOfActionRequestDocuments();
    assertEquals(50, actionRequestDocumentList.size());
    fileService.fileMe(actionRequestDocumentList);
    // TODO assert the file is not there initially in a @Before and then that it has been created
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
