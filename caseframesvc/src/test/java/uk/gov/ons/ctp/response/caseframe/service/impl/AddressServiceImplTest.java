package uk.gov.ons.ctp.response.caseframe.service.impl;

import org.junit.Assert;
import org.junit.Test;

import uk.gov.ons.ctp.common.jersey.TestHelper;

/**
 * tests for the address service postcode formatting method
 *
 */
public final class AddressServiceImplTest {

  private static final String FORMAT_POSTCODE = "formatPostcode";
  private static final String POSTCODE_FORMAT1_TEST1 = "M1 1AA";
  private static final String POSTCODE_FORMAT1_TEST2 = " m1  1aa";
  private static final String POSTCODE_FORMAT1_TEST3 = "M11aA ";
  private static final String POSTCODE_FORMAT1_RESULT = "M1   1AA";
  private static final String POSTCODE_FORMAT2_TEST1 = " M60 6XH ";
  private static final String POSTCODE_FORMAT2_TEST2 = "M606XH";
  private static final String POSTCODE_FORMAT2_RESULT = "M60  6XH";
  private static final String POSTCODE_FORMAT3_TEST1 = "EC1A 1BB";
  private static final String POSTCODE_FORMAT3_TEST2 = "EC1A1BB";
  private static final String POSTCODE_FORMAT3_RESULT = "EC1A 1BB";

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat1PostcodeSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT1_TEST1);
    Assert.assertEquals(POSTCODE_FORMAT1_RESULT, result);
  }

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat1PostcodeLeadingSpaceLowercase() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT1_TEST2);
    Assert.assertEquals(POSTCODE_FORMAT1_RESULT, result);
  }

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat1PostcodeTrailingSpaceMixedCase() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT1_TEST3);
    Assert.assertEquals(POSTCODE_FORMAT1_RESULT, result);
  }

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat2PostcodeLeadMidTrailingSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT2_TEST1);
    Assert.assertEquals(POSTCODE_FORMAT2_RESULT, result);
  }

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat2PostcodeNoSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT2_TEST2);
    Assert.assertEquals(POSTCODE_FORMAT2_RESULT, result);
  }

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat3PostcodeSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT3_TEST1);
    Assert.assertEquals(POSTCODE_FORMAT3_RESULT, result);
  }

  /**
   * a test
   *
   * @throws Exception something failed
   */
  @Test
  public void testFormat3PostcodeNoSpace() throws Exception {
    String result = (String) TestHelper.callPrivateMethodOfDefaultConstructableClass(AddressServiceImpl.class,
        FORMAT_POSTCODE, POSTCODE_FORMAT3_TEST2);
    Assert.assertEquals(POSTCODE_FORMAT3_RESULT, result);
  }
}
