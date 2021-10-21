package uk.gov.ons.ctp.response.casesvc.service.action.letter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.protobuf.ByteString;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.gov.ons.ctp.response.casesvc.CaseSvcApplication.PubSubOutboundPrintFileGateway;
import uk.gov.ons.ctp.response.casesvc.config.AppConfig;
import uk.gov.ons.ctp.response.casesvc.representation.action.LetterEntry;

@Slf4j
@Service
public class NotifyLetterService {

  @Autowired private AppConfig appConfig;

  @Autowired private UploadObjectGCS uploadObjectGCS;

  @Autowired private PubSubOutboundPrintFileGateway printFilePublisher;

  public boolean processPrintFile(String printFilename, List<LetterEntry> printFile) {
    boolean success = false;
    String dataFilename = FilenameUtils.removeExtension(printFilename).concat(".json");
    try {
      log.debug("creating json representation of print file");
      String json = createJson(printFile);
      ByteString data = ByteString.copyFromUtf8(json);

      String bucket = appConfig.getGcp().getBucket().getName();
      log.info("about to uploaded to bucket " + bucket);
      boolean uploaded = uploadObjectGCS.uploadObject(dataFilename, bucket, data.toByteArray());
      if (uploaded) {
        printFilePublisher.sendToPubSub(dataFilename, printFilename);
        log.info("print file pubsub successfully sent");
        success = true;
      }
    } catch (JsonProcessingException e) {
      log.error("unable to convert to json", e);
    }
    return success;
  }

  private String createJson(List<LetterEntry> printFile) throws JsonProcessingException {
    ObjectMapper mapper = new ObjectMapper();
    return mapper.writeValueAsString(printFile);
  }
}
