package uk.gov.ons.ctp.response.casesvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;
import uk.gov.ons.ctp.response.casesvc.message.sampleunitnotification.SampleUnitParent;

@Component
public class CaseCreator {

  private static final Logger log = LoggerFactory.getLogger(CaseCreator.class);
  @Autowired private ResourceLoader resourceLoader;
  @Autowired private IACServiceStub iacServiceStub;
  @Autowired private ObjectMapper objectMapper;

  public void postSampleUnit(
      String sampleUnitRef, String sampleUnitType, UUID sampleUnitId, UUID collectionExerciseId)
      throws Exception {
    PubSubTestEmulator pubSubEmulator = new PubSubTestEmulator();
    iacServiceStub.createIACStub();

    SampleUnitParent sampleUnit = new SampleUnitParent();
    sampleUnit.setCollectionExerciseId(collectionExerciseId.toString());
    sampleUnit.setId(sampleUnitId.toString());
    sampleUnit.setActiveEnrolment(true);
    sampleUnit.setSampleUnitRef(sampleUnitRef);
    sampleUnit.setCollectionInstrumentId(UUID.randomUUID().toString());
    sampleUnit.setPartyId(UUID.randomUUID().toString());
    sampleUnit.setSampleUnitType(sampleUnitType);

    String message = objectMapper.writeValueAsString(sampleUnit);
    pubSubEmulator.publishSampleCaseCreationMessage(message);
  }
}
