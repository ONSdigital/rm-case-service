package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uk.gov.ons.ctp.response.casesvc.domain.model.UnlinkedCaseReceipt;

import javax.inject.Named;
import javax.transaction.Transactional;

@Named
@Transactional
public interface UnlinkedCaseReceiptRepository extends JpaRepository<UnlinkedCaseReceipt, String> {
}
