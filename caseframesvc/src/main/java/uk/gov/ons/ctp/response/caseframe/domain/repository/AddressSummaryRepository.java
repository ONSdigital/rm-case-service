package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;

/**
 * JPA Data Repository.
 */
@Repository
public interface AddressSummaryRepository extends JpaRepository<AddressSummary, String> {
  /**
   * find the AddressSummary by the MSOA11 code.
   * @param msoaid to find by
   * @return the address summary or null if not found
   */
  List<AddressSummary> findByMsoa11cd(String msoaid);

}
