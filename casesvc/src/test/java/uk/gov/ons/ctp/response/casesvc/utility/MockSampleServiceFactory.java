package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.List;

import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.common.FixtureHelper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

/**
 * Created by Martin.Humphrey on 23/2/2016.
 */
public final class MockSampleServiceFactory {

  public static final String SAMPLE1_NAME = "Residential";
  public static final String SAMPLE2_NAME = "Hotels_Guest_Houses";
  public static final String SAMPLE3_NAME = "Care_Homes";
  public static final String SAMPLE1_DESC = "Households";
  public static final String SAMPLE2_DESC = "Hotels Guest Houses Bed and Breakfasts";
  public static final String SAMPLE3_DESC = "Care Homes";
  public static final String SAMPLE1_CRITERIA = "addresstype = 'HH'";
  public static final String SAMPLE2_CRITERIA = "addresstype = 'CE' and estabtype in ('25')";
  public static final String SAMPLE3_CRITERIA = "addresstype = 'CE' and estabtype in ('22')";
  public static final Integer SAMPLE1_CASETYPEID = 1;
  public static final Integer SAMPLE2_CASETYPEID = 2;
  public static final Integer SAMPLE3_CASETYPEID = 3;
  public static final String SURVEY1_NAME = "survey1";
  public static final String SURVEY2_NAME = "survey2";
  public static final String SURVEY3_NAME = "survey3";
  public static final Integer SAMPLEID = 3;
  public static final Integer NON_EXISTING_SAMPLEID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final String GEOGRAPHY_TYPE = "LA";
  public static final String GEOGRAPHY_CODE = "E07000163";

  /**
   * provide method
   * 
   * @return mocked service
   */
  public static SampleService provide() {

    final SampleService mockedService = Mockito.mock(SampleService.class);

    try {
      List<Sample> samples = FixtureHelper.loadClassFixtures(Sample[].class);
      Mockito.when(mockedService.findSamples()).thenAnswer(new Answer<List<Sample>>() {
        public List<Sample> answer(final InvocationOnMock invocation)
            throws Throwable {
          return samples;
        }
      });

      Mockito.when(mockedService.findSampleBySampleId(SAMPLEID)).thenAnswer(new Answer<Sample>() {
        public Sample answer(final InvocationOnMock invocation)
            throws Throwable {
          return samples.get(2);
        }
      });

      Mockito.when(mockedService.findSampleBySampleId(UNCHECKED_EXCEPTION))
          .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

      Mockito.when(mockedService.findSampleBySampleId(NON_EXISTING_SAMPLEID)).thenAnswer(new Answer<Sample>() {
        public Sample answer(final InvocationOnMock invocation)
            throws Throwable {
          return null;
        }
      });

      Mockito.when(mockedService.generateCases(SAMPLEID, GEOGRAPHY_TYPE, GEOGRAPHY_CODE)).thenReturn(true);

    } catch (Throwable t) {
      throw new RuntimeException(t);
    }
    return mockedService;
  }
}
