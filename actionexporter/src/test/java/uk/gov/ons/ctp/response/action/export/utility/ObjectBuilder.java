package uk.gov.ons.ctp.response.action.export.utility;

import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class to build objects required in tests
 */
public class ObjectBuilder {
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
