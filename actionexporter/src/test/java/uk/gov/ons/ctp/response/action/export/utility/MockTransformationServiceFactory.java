package uk.gov.ons.ctp.response.action.export.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.SftpMessage;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

import static org.mockito.Matchers.any;

/**
 * Mock TransformationService response HK2 JSE JSR-330 dependency injection factory
 */
public class MockTransformationServiceFactory implements Factory<TransformationService> {
  /**
   * provide method
   *
   * @return mocked service
   */
  public TransformationService provide() {
    final TransformationService mockedService = Mockito.mock(TransformationService.class);

    Mockito.when(mockedService.processActionRequest(any(ActionRequestDocument.class))).thenAnswer(new Answer<SftpMessage>() {
      public SftpMessage answer(final InvocationOnMock invocation) throws Throwable {
        return buildSftpMessage();
      }
    });

    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final TransformationService t) {
  }

  private SftpMessage buildSftpMessage() {
    SftpMessage message = new SftpMessage();
    message.setOutputStreams(new HashMap());
    return message;
  }
}