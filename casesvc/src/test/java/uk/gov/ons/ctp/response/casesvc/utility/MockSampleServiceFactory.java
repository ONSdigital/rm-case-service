package uk.gov.ons.ctp.response.casesvc.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;
import uk.gov.ons.ctp.response.casesvc.service.SampleService;

/**
 * Created by Martin.Humphrey on 23/2/2016.
 */
public final class MockSampleServiceFactory implements Factory<SampleService> {

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
  public static final Integer SURVEYID = 1;
  public static final Integer SAMPLEID = 3;
  public static final Integer NON_EXISTING_SAMPLEID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";
  public static final String GEOGRAPHY_TYPE = "LA";
  public static final String GEOGRAPHY_CODE = "E07000163";

  /**
   * provide method
   * @return mocked service
   */
  public SampleService provide() {

    final SampleService mockedService = Mockito.mock(SampleService.class);

    Mockito.when(mockedService.findSamples()).thenAnswer(new Answer<List<Sample>>() {
      public List<Sample> answer(final InvocationOnMock invocation)
          throws Throwable {
        List<Sample> result = new ArrayList<Sample>();
        result.add(new Sample(1, SAMPLE1_NAME, SAMPLE1_DESC, SAMPLE1_CRITERIA, SAMPLE1_CASETYPEID, SURVEYID));
        result.add(new Sample(2, SAMPLE2_NAME, SAMPLE2_DESC, SAMPLE2_CRITERIA, SAMPLE2_CASETYPEID, SURVEYID));
        result.add(new Sample(3, SAMPLE3_NAME, SAMPLE3_DESC, SAMPLE3_CRITERIA, SAMPLE3_CASETYPEID, SURVEYID));
        return result;
      }
    });

    Mockito.when(mockedService.findSampleBySampleId(SAMPLEID)).thenAnswer(new Answer<Sample>() {
      public Sample answer(final InvocationOnMock invocation)
          throws Throwable {
        return new Sample(3, SAMPLE3_NAME, SAMPLE3_DESC, SAMPLE3_CRITERIA, SAMPLE3_CASETYPEID, SURVEYID);
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

    //Mockito.when(mockedService.generateCases(SAMPLEID, GEOGRAPHY_TYPE, GEOGRAPHY_CODE)).thenReturn(true);

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final SampleService t) {
  }
}
