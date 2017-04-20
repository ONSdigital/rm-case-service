package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UnlinkedCaseReceiptRepository extends JpaRepository<UnlinkedCaseReceipt, String> {
}
