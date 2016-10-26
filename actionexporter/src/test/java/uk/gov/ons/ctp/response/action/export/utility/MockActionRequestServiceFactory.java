package uk.gov.ons.ctp.response.action.export.utility;


import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.ActionRequestService;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * Mock ActionRequestService response HK2 JSE JSR-330 dependency injection factory
 */
public class MockActionRequestServiceFactory implements Factory<ActionRequestService> {

  public final static int NON_EXISTING_ACTION_ID = 1;

  /**
   * provide method
   *
   * @return mocked service
   */
  public ActionRequestService provide() {
    final ActionRequestService mockedService = Mockito.mock(ActionRequestService.class);

    Mockito.when(mockedService.retrieveAllActionRequestDocuments()).thenAnswer(new Answer<List<ActionRequestDocument>>() {
      public List<ActionRequestDocument> answer(final InvocationOnMock invocation) throws Throwable {
        List<ActionRequestDocument> result = new ArrayList<>();
        for (int i = 0; i < 3; i++){
          result.add(buildActionRequestDocument(i));
        }
        return result;
      }
    });

    Mockito.when(mockedService.retrieveActionRequestDocument(BigInteger.valueOf(NON_EXISTING_ACTION_ID))).thenAnswer(new Answer<ActionRequestDocument>() {
      public ActionRequestDocument answer(final InvocationOnMock invocation) throws Throwable {
        return null;
      }
    });

    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final ActionRequestService t) {
  }

  private static ActionRequestDocument buildActionRequestDocument(int actionId) {
    ActionRequestDocument actionRequestDocument = new ActionRequestDocument();
    actionRequestDocument.setActionId(BigInteger.valueOf(actionId));
    return actionRequestDocument;
  }
}
