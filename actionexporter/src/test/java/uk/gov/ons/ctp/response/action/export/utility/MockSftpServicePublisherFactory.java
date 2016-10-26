package uk.gov.ons.ctp.response.action.export.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;

import static org.mockito.Matchers.any;

/**
 * Mock SftpServicePublisher response HK2 JSE JSR-330 dependency injection factory
 */
public class MockSftpServicePublisherFactory implements Factory<SftpServicePublisher> {
  /**
   * provide method
   *
   * @return mocked service
   */
  public SftpServicePublisher provide() {
    final SftpServicePublisher mockedService = Mockito.mock(SftpServicePublisher.class);

    Mockito.when(mockedService.sendMessage(any(String.class), any(), any())).thenAnswer(new Answer<byte[]>() {
      public byte[] answer(final InvocationOnMock invocation) throws Throwable {
        return "Any string".getBytes();
      }
    });
    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final SftpServicePublisher t) {
  }
}
