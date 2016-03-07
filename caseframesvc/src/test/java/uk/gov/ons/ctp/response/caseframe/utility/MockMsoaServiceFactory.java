package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.Address;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.service.MsoaService;

/**
 * Created by philippe.brossier on 2/23/16.
 */
public final class MockMsoaServiceFactory implements Factory<MsoaService> {

  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final Long ADDRESS_SUMMARY1_UPRN = 12345L;
  public static final Long ADDRESS_SUMMARY2_UPRN = 6789L;
  public static final String ADDRESS_SUMMARY1_TYPE = "CE";
  public static final String ADDRESS_SUMMARY2_TYPE = "HH";
  public static final String LAD_FOR_MSOA123 = "lad123";
  public static final String MSOA_WITH_CODE_MSOA123 = "msoa123";
  public static final String MSOA_WITH_CODE_204 = "msoa204";
  public static final String MSOA_WITH_NON_EXISTING_CODE = "random";
  public static final String MSOA_WITH_CODE_CHECKED_EXCEPTION = "sse";
  public static final String NAME = "_name";

  /**
   * provide method
   * @return mocked service
   */
  public MsoaService provide() {
    final MsoaService mockedService = Mockito.mock(MsoaService.class);
    Mockito.when(mockedService.findById(MSOA_WITH_CODE_MSOA123)).thenAnswer(new Answer<Msoa>() {
      public Msoa answer(final InvocationOnMock invocation)
          throws Throwable {
        String msoaCode = (String) invocation.getArguments()[0];
        Msoa msoa = new Msoa();
        msoa.setMsoa11cd(msoaCode);
        msoa.setMsoa11nm(msoaCode + NAME);
        msoa.setLad12cd(LAD_FOR_MSOA123);
        return msoa;
      }
    });

    Mockito.when(mockedService.findById(MSOA_WITH_NON_EXISTING_CODE)).thenAnswer(new Answer<Msoa>() {
      public Msoa answer(final InvocationOnMock invocation)
          throws Throwable {
        return null;
      }
    });

    Mockito.when(mockedService.findById(MSOA_WITH_CODE_CHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(MockRegionServiceFactory.OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findAllAddressSummariesByMsoaid(MSOA_WITH_CODE_MSOA123))
        .thenAnswer(new Answer<List<Address>>() {
          public List<Address> answer(final InvocationOnMock invocation)
              throws Throwable {
            String msoaCode = (String) invocation.getArguments()[0];
            Address address1 = new Address();
            address1.setMsoa11cd(msoaCode);
            address1.setLad12cd(MockLocalAuthorityServiceFactory.LAD_WITH_CODE_LAD123);
            address1.setRegion11cd(MockRegionServiceFactory.REGION_WITH_CODE_REG123);
            address1.setUprn(ADDRESS_SUMMARY1_UPRN);
            address1.setAddressType(ADDRESS_SUMMARY1_TYPE);
            Address address2 = new Address();
            address2.setMsoa11cd(msoaCode);
            address2.setLad12cd(MockLocalAuthorityServiceFactory.LAD_WITH_CODE_LAD123);
            address2.setRegion11cd(MockRegionServiceFactory.REGION_WITH_CODE_REG123);
            address2.setUprn(ADDRESS_SUMMARY2_UPRN);
            address2.setAddressType(ADDRESS_SUMMARY2_TYPE);
            List<Address> result = new ArrayList<>();
            result.add(address1);
            result.add(address2);
            return result;
          }
        });

    Mockito.when(mockedService.findAllAddressSummariesByMsoaid(MSOA_WITH_CODE_204))
        .thenAnswer(new Answer<List<Address>>() {
          public List<Address> answer(final InvocationOnMock invocation)
              throws Throwable {
            return new ArrayList<Address>();
          }
        });

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final MsoaService t) {
  }
}
