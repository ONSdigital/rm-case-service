package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.service.LocalAuthorityService;

/**
 * Created by philippe.brossier on 2/22/16.
 */
public final class MockLocalAuthorityServiceFactory implements Factory<LocalAuthorityService> {

  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final String MSOA1_CODE = "msoa1code";
  public static final String MSOA1_NAME = "msoa1name";
  public static final String MSOA2_CODE = "msoa2code";
  public static final String MSOA2_NAME = "msoa2name";
  public static final String LAD_WITH_CODE_LAD123 = "lad123";
  public static final String LAD_WITH_CODE_204 = "lad204";
  public static final String LAD_WITH_NON_EXISTING_CODE = "random";
  public static final String LAD_WITH_CODE_CHECKED_EXCEPTION = "sse";
  public static final String REGION_CODE_FOR_LAD123 = "reg123";
  public static final String NAME = "_name";

  /**
   * provide method
   * @return mocked service
   */
  public LocalAuthorityService provide() {
    final LocalAuthorityService mockedService = Mockito.mock(LocalAuthorityService.class);
    Mockito.when(mockedService.findById(LAD_WITH_CODE_LAD123)).thenAnswer(new Answer<LocalAuthority>() {
      public LocalAuthority answer(final InvocationOnMock invocation)
          throws Throwable {
        String ladCode = (String) invocation.getArguments()[0];
        LocalAuthority lad = new LocalAuthority();
        lad.setLad12cd(ladCode);
        lad.setLad12nm(ladCode + NAME);
        lad.setRgn11cd(REGION_CODE_FOR_LAD123);
        return lad;
      }
    });

    Mockito.when(mockedService.findById(LAD_WITH_NON_EXISTING_CODE)).thenAnswer(new Answer<LocalAuthority>() {
      public LocalAuthority answer(final InvocationOnMock invocation)
          throws Throwable {
        return null;
      }
    });

    Mockito.when(mockedService.findById(LAD_WITH_CODE_CHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(MockRegionServiceFactory.OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findAllMsoasByLadid(LAD_WITH_CODE_LAD123)).thenAnswer(new Answer<List<Msoa>>() {
      public List<Msoa> answer(final InvocationOnMock invocation)
          throws Throwable {
        String ladCode = (String) invocation.getArguments()[0];
        Msoa msoa1 = new Msoa();
        msoa1.setLad12cd(ladCode);
        msoa1.setMsoa11cd(MSOA1_CODE);
        msoa1.setMsoa11nm(MSOA1_NAME);
        Msoa msoa2 = new Msoa();
        msoa2.setLad12cd(ladCode);
        msoa2.setMsoa11cd(MSOA2_CODE);
        msoa2.setMsoa11nm(MSOA2_NAME);
        List<Msoa> result = new ArrayList<>();
        result.add(msoa1);
        result.add(msoa2);
        return result;
      }
    });

    Mockito.when(mockedService.findAllMsoasByLadid(LAD_WITH_CODE_204)).thenAnswer(new Answer<List<Msoa>>() {
      public List<Msoa> answer(final InvocationOnMock invocation)
          throws Throwable {
        return new ArrayList<Msoa>();
      }
    });

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final LocalAuthorityService t) {
  }

}
