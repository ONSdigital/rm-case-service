package uk.gov.ons.ctp.response.caseframe.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Region;
import uk.gov.ons.ctp.response.caseframe.service.RegionService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philippe.brossier on 2/22/16.
 */
public final class MockRegionServiceFactory implements Factory<RegionService> {

  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final String LAD1_CODE = "lad1code";
  public static final String LAD1_NAME = "lad1name";
  public static final String LAD2_CODE = "lad2code";
  public static final String LAD2_NAME = "lad2name";
  public static final String NAME = "_name";
  public static final String REGION_WITH_CODE_204 = "reg204";
  public static final String REGION_WITH_CODE_REG123 = "reg123";
  public static final String REGION_WITH_NON_EXISTING_CODE = "random";
  public static final String REGION_WITH_CODE_CHECKED_EXCEPTION = "sse";
  public static final String REGION1_CODE = "reg1code";
  public static final String REGION1_NAME = "reg1name";
  public static final String REGION2_CODE = "reg2code";
  public static final String REGION2_NAME = "reg2name";

  /**
   * provide method
   * 
   * @return mocked service
   */
  public RegionService provide() {
    final RegionService mockedService = Mockito.mock(RegionService.class);
    Mockito.when(mockedService.findByRegionId(REGION_WITH_CODE_REG123)).thenAnswer(new Answer<Region>() {
      public Region answer(InvocationOnMock invocation)
          throws Throwable {
        String regionCode = (String) invocation.getArguments()[0];
        Region region = new Region();
        region.setRgn11cd(regionCode);
        region.setRgn11nm(regionCode + NAME);
        return region;
      }
    });
    Mockito.when(mockedService.findByRegionId(REGION_WITH_NON_EXISTING_CODE)).thenAnswer(new Answer<Region>() {
      public Region answer(InvocationOnMock invocation)
          throws Throwable {
        return null;
      }
    });
    Mockito.when(mockedService.findByRegionId(REGION_WITH_CODE_CHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findAll()).thenAnswer(new Answer<List<Region>>() {
      public List<Region> answer(final InvocationOnMock invocation)
          throws Throwable {
        Region region1 = new Region();
        region1.setRgn11cd(REGION1_CODE);
        region1.setRgn11nm(REGION1_NAME);
        Region region2 = new Region();
        region2.setRgn11cd(REGION2_CODE);
        region2.setRgn11nm(REGION2_NAME);
        List<Region> result = new ArrayList<>();
        result.add(region1);
        result.add(region2);
        return result;
      }
    });

    Mockito.when(mockedService.findAllLadsByRegionid(REGION_WITH_CODE_REG123))
        .thenAnswer(new Answer<List<LocalAuthority>>() {
          public List<LocalAuthority> answer(final InvocationOnMock invocation)
              throws Throwable {
            String regionCode = (String) invocation.getArguments()[0];
            LocalAuthority lad1 = new LocalAuthority();
            lad1.setRgn11cd(regionCode);
            lad1.setLad12cd(LAD1_CODE);
            lad1.setLad12nm(LAD1_NAME);
            LocalAuthority lad2 = new LocalAuthority();
            lad2.setRgn11cd(regionCode);
            lad2.setLad12cd(LAD2_CODE);
            lad2.setLad12nm(LAD2_NAME);
            List<LocalAuthority> result = new ArrayList<>();
            result.add(lad1);
            result.add(lad2);
            return result;
          }
        });

    Mockito.when(mockedService.findAllLadsByRegionid(REGION_WITH_CODE_204))
        .thenAnswer(new Answer<List<LocalAuthority>>() {
          public List<LocalAuthority> answer(final InvocationOnMock invocation)
              throws Throwable {
            return new ArrayList<LocalAuthority>();
          }
        });

    return mockedService;
  }

  /**
   * dispose method
   * 
   * @param t service to dispose
   */
  public void dispose(final RegionService t) {
  }
}
