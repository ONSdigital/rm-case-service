package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.ActionPlanMapping;
import uk.gov.ons.ctp.response.casesvc.service.ActionPlanMappingService;

/**
 */
public final class MockActionPlanMappingServiceFactory implements Factory<ActionPlanMappingService> {

  public static final Integer MAPPING_ID = 1;
  /**
   * provide method
   * 
   * @return mocked service
   */
  public ActionPlanMappingService provide() {

    final ActionPlanMappingService mockedService = Mockito.mock(ActionPlanMappingService.class);

    try {
      List<ActionPlanMapping> mappings = FixtureHelper.loadClassFixtures(ActionPlanMapping[].class);

      Mockito.when(mockedService.findActionPlanMapping(MAPPING_ID)).thenAnswer(new Answer<ActionPlanMapping>() {
        public ActionPlanMapping answer(final InvocationOnMock invocation)
            throws Throwable {
          return mappings.get(0);
        }
      });

    } catch (Throwable t) {
      throw new RuntimeException(t);
    }

    return mockedService;
  }

  /**
   * dispose method
   * 
   * @param t service to dispose
   */
  public void dispose(final ActionPlanMappingService t) {
  }
}
