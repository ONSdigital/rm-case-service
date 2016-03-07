package uk.gov.ons.ctp.response.caseframe.utility;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import uk.gov.ons.ctp.response.caseframe.domain.model.Address;
import uk.gov.ons.ctp.response.caseframe.service.AddressService;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by philippe.brossier on 2/23/16.
 */
public final class MockAddressServiceFactory implements Factory<AddressService> {

  public static final Long ADDRESS_NON_EXISTING_UPRN = 999L;
  public static final Long ADDRESS_UPRN = 123L;
  public static final Long ADDRESS_WITH_UPRN_CHECKED_EXCEPTION = 666L;

  public static final String ADDRESS_NON_EXISTING_POSTCODE = "PORANDOM";
  public static final String ADDRESS_POSTCODE = "PO15 5RR";
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * @return mocked service
   */
  public AddressService provide() {
    final AddressService mockedService = Mockito.mock(AddressService.class);
    Mockito.when(mockedService.findByUprn(ADDRESS_UPRN)).thenAnswer(new Answer<Address>() {
      public Address answer(final InvocationOnMock invocation)
          throws Throwable {
        Long addressUprn = (Long) invocation.getArguments()[0];
        Address address = AddressBuilder.address().uprn(addressUprn).buildAddress();
        return address;
      }
    });
    Mockito.when(mockedService.findByPostcode(ADDRESS_POSTCODE)).thenAnswer(new Answer<List<Address>>() {
      public List<Address> answer(final InvocationOnMock invocation)
          throws Throwable {
        String addressPostcode = (String) invocation.getArguments()[0];
        Address address = AddressBuilder.address().postcode(addressPostcode).buildAddress();

        List<Address> result = new ArrayList<>();
        result.add(address);
        return result;
      }
    });
    Mockito.when(mockedService.findByUprn(ADDRESS_NON_EXISTING_UPRN)).thenAnswer(new Answer<List<Address>>() {
      public List<Address> answer(final InvocationOnMock invocation)
          throws Throwable {
        return null;
      }
    });
    Mockito.when(mockedService.findByUprn(ADDRESS_WITH_UPRN_CHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final AddressService t) {
  }
}
