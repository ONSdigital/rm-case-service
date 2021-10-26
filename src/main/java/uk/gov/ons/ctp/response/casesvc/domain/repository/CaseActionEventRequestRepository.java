package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionEventRequest;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseActionEventRequest.ActionEventRequestStatus;

public interface CaseActionEventRequestRepository
    extends JpaRepository<CaseActionEventRequest, Integer> {
  List<CaseActionEventRequest> findByCollectionExerciseIdAndEventTag(
      UUID collectionExerciseId, String eventTag);

  List<CaseActionEventRequest> findByStatus(ActionEventRequestStatus status);
}
