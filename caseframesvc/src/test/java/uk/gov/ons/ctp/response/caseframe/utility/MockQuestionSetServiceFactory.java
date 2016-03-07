package uk.gov.ons.ctp.response.caseframe.utility;

import java.util.ArrayList;
import java.util.List;

import org.glassfish.hk2.api.Factory;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import uk.gov.ons.ctp.response.caseframe.domain.model.QuestionSet;
import uk.gov.ons.ctp.response.caseframe.service.QuestionSetService;

/**
 * Created by Martin.Humphrey on 23/2/2016.
 */
public final class MockQuestionSetServiceFactory implements Factory<QuestionSetService> {

  public static final String QUESTIONSET1_NAME = "HH";
  public static final String QUESTIONSET2_NAME = "CE";
  public static final String QUESTIONSET1_DESC = "Households";
  public static final String QUESTIONSET2_DESC = "Communal Establishments";
  public static final String QUESTIONSETNAME = "HH";
  public static final String NON_EXISTING_QUESTIONSETNAME = "XXXX";
  public static final String UNCHECKED_EXCEPTION = "YYYY";
  public static final String OUR_EXCEPTION_MESSAGE = "this is what we throw";

  /**
   * provide method
   * @return mocked service
   */
  public QuestionSetService provide() {

    final QuestionSetService mockedService = Mockito.mock(QuestionSetService.class);

    Mockito.when(mockedService.findQuestionSets()).thenAnswer(new Answer<List<QuestionSet>>() {
      public List<QuestionSet> answer(final InvocationOnMock invocation)
          throws Throwable {
        List<QuestionSet> result = new ArrayList<QuestionSet>();
        result.add(new QuestionSet(QUESTIONSET1_NAME, QUESTIONSET1_DESC));
        result.add(new QuestionSet(QUESTIONSET2_NAME, QUESTIONSET2_DESC));
        return result;
      }
    });

    Mockito.when(mockedService.findQuestionSetByQuestionSet(QUESTIONSETNAME)).thenAnswer(new Answer<QuestionSet>() {
      public QuestionSet answer(final InvocationOnMock invocation)
          throws Throwable {
        return new QuestionSet(QUESTIONSET1_NAME, QUESTIONSET1_DESC);
      }
    });

    Mockito.when(mockedService.findQuestionSetByQuestionSet(UNCHECKED_EXCEPTION))
        .thenThrow(new IllegalArgumentException(OUR_EXCEPTION_MESSAGE));

    Mockito.when(mockedService.findQuestionSetByQuestionSet(NON_EXISTING_QUESTIONSETNAME))
        .thenAnswer(new Answer<QuestionSet>() {
          public QuestionSet answer(final InvocationOnMock invocation)
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
  public void dispose(final QuestionSetService t) {
  }
}
