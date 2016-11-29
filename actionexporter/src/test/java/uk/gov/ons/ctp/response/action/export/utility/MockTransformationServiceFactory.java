package uk.gov.ons.ctp.response.action.export.utility;

import static org.mockito.Matchers.any;

import java.io.ByteArrayOutputStream;
import java.util.Collections;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.ExportMessage;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

/**
 * Mock TransformationService response HK2 JSE JSR-330 dependency injection
 * factory
 */
public class MockTransformationServiceFactory implements Factory<TransformationService> {
  /**
   * provide method
   *
   * @return mocked service
   */
  public TransformationService provide() {
    final TransformationService mockedService = Mockito.mock(TransformationService.class);

    try {
      Mockito.when(mockedService.processActionRequest(any(ExportMessage.class), any(ActionRequestDocument.class)))
          .thenAnswer(new Answer<ExportMessage>() {
            public ExportMessage answer(final InvocationOnMock invocation) throws Throwable {
              Object[] args = invocation.getArguments();
              return buildSftpMessage((ExportMessage) args[0]);
            }
          });
    } catch (CTPException e) {
      // Exception from mockedService.processActionRequest
    }
    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final TransformationService t) {
  }

  private ExportMessage buildSftpMessage(ExportMessage message) {
    message.getActionRequestIds().put("dummy", Collections.singletonList("12345"));
    message.getOutputStreams().put("dummy", new ByteArrayOutputStream());
    return message;
  }
}