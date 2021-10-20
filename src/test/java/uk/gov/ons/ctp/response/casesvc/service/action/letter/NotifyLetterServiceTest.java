package uk.gov.ons.ctp.response.casesvc.service.action.letter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import java.util.concurrent.ExecutionException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.config.Bucket;
import uk.gov.ons.ctp.response.casesvc.config.GCP;

@RunWith(MockitoJUnitRunner.class)
public class NotifyLetterServiceTest {
  @InjectMocks private NotifyLetterService notifyLetterService;
  @Mock private CaseSvcApplication.PubSubOutboundPrintFileGateway publisher;
  @Mock UploadObjectGCS uploadObjectGCS;
  @Mock AppConfig appConfig;
  @Mock GCP gcp;
  @Mock Bucket bucket;

  @Test
  public void testConvertToPrintFile() throws ExecutionException, InterruptedException {
    given(uploadObjectGCS.uploadObject(anyString(), anyString(), any())).willReturn(true);
    given(appConfig.getGcp()).willReturn(gcp);
    given(gcp.getBucket()).willReturn(bucket);
    given(bucket.getName()).willReturn("test-bucket");

    notifyLetterService.processPrintFile(
        "test.csv", ProcessEventServiceTestData.buildListOfLetterEntries());

    verify(publisher).sendToPubSub(anyString(), anyString());
    verify(uploadObjectGCS).uploadObject(anyString(), anyString(), any());
  }
}
