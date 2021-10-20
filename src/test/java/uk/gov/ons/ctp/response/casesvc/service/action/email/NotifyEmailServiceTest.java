package uk.gov.ons.ctp.response.casesvc.service.action.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication;
import uk.gov.ons.ctp.response.casesvc.representation.action.NotifyModel;

@RunWith(MockitoJUnitRunner.class)
public class NotifyEmailServiceTest {

  @InjectMocks private NotifyEmailService notifyEmailService;

  @Mock private CaseSvcApplication.PubSubOutboundEmailGateway publisher;

  @Spy private ObjectMapper objectMapper = new ObjectMapper();

  @Mock private com.google.api.core.ApiFuture<String> ApiFuture;

  private static final String emailJson =
      "{\"notify\":{"
          + "\"classifiers\":{"
          + "\"survey\":\"\","
          + "\"region\":\"\""
          + "},"
          + "\"personalisation\":{"
          + "\"firstname\":\"Joe\","
          + "\"lastname\":null,"
          + "\"reporting unit reference\":null,"
          + "\"survey id\":null,"
          + "\"survey name\":null,"
          + "\"return by date\":null,"
          + "\"RU name\":null,"
          + "\"trading style\":null,"
          + "\"respondent period\":null"
          + "},"
          + "\"email_address\":null,"
          + "\"reference\":null"
          + "}}";

  @Test
  public void willCallPublisherWithEncodedJSONString() {
    NotifyModel.Notify.Classifiers classifiers =
        NotifyModel.Notify.Classifiers.builder().region("").surveyRef("").build();

    NotifyModel.Notify.Personalisation personalisation =
        NotifyModel.Notify.Personalisation.builder()
            .firstname("Joe")
            .lastname(null)
            .reportingUnitReference(null)
            .returnByDate(null)
            .tradingSyle(null)
            .ruName(null)
            .surveyId(null)
            .surveyName(null)
            .respondentPeriod(null)
            .build();

    notifyEmailService.processEmail(
        new NotifyModel(
            NotifyModel.Notify.builder()
                .classifiers(classifiers)
                .personalisation(personalisation)
                .emailAddress(null)
                .reference(null)
                .build()));

    Mockito.verify(publisher).sendToPubSub(emailJson);
  }
}
