package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;
import uk.gov.ons.ctp.response.caseframe.service.SurveyService;

/**
 * Created by Martin.Humphrey on 23/2/2016.
 */
public final class MockSurveyServiceFactory implements Factory<SurveyService> {

  public static final String SURVEY1_NAME = "2016_Test";
  public static final String SURVEY2_NAME = "2017_Test";
  public static final String SURVEY3_NAME = "2019_Test";
  public static final String SURVEY4_NAME = "2021_Census";
  public static final String SURVEY1_DESC = "2016_Census Test";
  public static final String SURVEY2_DESC = "2017 Census Test";
  public static final String SURVEY3_DESC = "2019 Census Test";
  public static final String SURVEY4_DESC = "2021 Census Operations";
  public static final Integer SURVEYID = 4;
  public static final Integer NON_EXISTING_SURVEYID = 998;
  public static final Integer UNCHECKED_EXCEPTION = 999;
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * @return mocked service
   */
  public SurveyService provide() {

    final SurveyService mockedService = Mockito.mock(SurveyService.class);

    Mockito.when(mockedService.findSurveys()).thenAnswer(new Answer<List<Survey>>() {
      public List<Survey> answer(final InvocationOnMock invocation)
          throws Throwable {
        List<Survey> result = new ArrayList<Survey>();
        result.add(new Survey(1, SURVEY1_NAME, SURVEY1_DESC));
        result.add(new Survey(2, SURVEY2_NAME, SURVEY2_DESC));
        result.add(new Survey(3, SURVEY3_NAME, SURVEY3_DESC));
        result.add(new Survey(4, SURVEY4_NAME, SURVEY4_DESC));
        return result;
      }
    });

    Mockito.when(mockedService.findSurveyBySurveyId(SURVEYID)).thenAnswer(new Answer<Survey>() {
      public Survey answer(final InvocationOnMock invocation)
          throws Throwable {
        return new Survey(4, SURVEY4_NAME, SURVEY4_DESC);
      }
    });

    Mockito.when(mockedService.findSurveyBySurveyId(UNCHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findSurveyBySurveyId(NON_EXISTING_SURVEYID)).thenAnswer(new Answer<Survey>() {
      public Survey answer(final InvocationOnMock invocation)
          throws Throwable {
        return null;
      }
    });

    return mockedService;
  }

  /**
   * dispose method
   * @param t service to dispose
   */
  public void dispose(final SurveyService t) {
  }
}
