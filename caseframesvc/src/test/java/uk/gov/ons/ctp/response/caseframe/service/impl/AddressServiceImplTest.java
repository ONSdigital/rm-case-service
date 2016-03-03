package uk.gov.ons.ctp.response.caseframe.service.impl;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.ons.ctp.common.jersey.TestHelper;

public class AddressServiceImplTest {

  private static final String FORMAT_POSTCODE = "formatPostcode";
  private static final String POSTCODE_WITH_SPACE = "PO15 5RR";
  private static final String POSTCODE_WITH_NO_SPACE = "PO155RR";

  @Test
  public void testFormatPostcodeWithSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class, FORMAT_POSTCODE, POSTCODE_WITH_SPACE);
    Assert.assertEquals(POSTCODE_WITH_SPACE, result);
  }

  @Test
  public void testFormatPostcodeWithNoSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class, FORMAT_POSTCODE, POSTCODE_WITH_NO_SPACE);
    Assert.assertEquals(POSTCODE_WITH_SPACE, result);
  }
}
