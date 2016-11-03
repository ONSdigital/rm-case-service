package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.service.CaseGroupService;

/**
 */
public final class MockCaseGroupServiceFactory implements Factory<CaseGroupService> {

  public static final Integer NON_EXISTENT_CASE_GROUP_ID = 99;
  public static final Integer CASE_GROUP_ID = 1;
  public static final Integer SAMPLE_ID = 2;
  public static final Long UPRN = 3L;
  /**
   * provide method
   * @return mocked service
   */
  public CaseGroupService provide() {

    final CaseGroupService mockedService = Mockito.mock(CaseGroupService.class);

    Mockito.when(mockedService.findCaseGroupByCaseGroupId(CASE_GROUP_ID)).thenAnswer(new Answer<CaseGroup>() {
      public CaseGroup answer(final InvocationOnMock invocation)
          throws Throwable {
        CaseGroup result = CaseGroup.builder().caseGroupId(CASE_GROUP_ID).sampleId(SAMPLE_ID).uprn(UPRN).build();
        return result;
      }
    });

    Mockito.when(mockedService.findCaseGroupsByUprn(UPRN)).thenAnswer(new Answer<List<CaseGroup>>() {
      public List<CaseGroup> answer(final InvocationOnMock invocation)
          throws Throwable {
        List<CaseGroup> result = new ArrayList<>();
        result.add(CaseGroup.builder().caseGroupId(CASE_GROUP_ID).sampleId(SAMPLE_ID).uprn(UPRN).build());
        return result;
      }
    });

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final CaseGroupService t) {
  }
}
