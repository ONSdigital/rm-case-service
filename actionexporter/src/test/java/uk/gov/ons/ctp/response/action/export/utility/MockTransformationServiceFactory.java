package uk.gov.ons.ctp.response.action.export.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

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
    return mockedService;
  }

  /**
   * dispose method
   *
   * @param t service to dispose
   */
  public void dispose(final TransformationService t) {
  }
}