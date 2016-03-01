package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;

/**
 * JPA Data Respository
 */
@Repository
public interface AddressSummaryRepository extends JpaRepository<AddressSummary, String> {
    List<AddressSummary> findByMsoa11cd(String msoaid);
}
