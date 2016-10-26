package uk.gov.ons.ctp.response.action.export.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import uk.gov.ons.ctp.response.action.export.message.SftpServicePublisher;

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
