package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.ReportType;

/**
 * JPA Data Repository.
 */
@Repository
public interface ReportTypeRepository extends JpaRepository<ReportType, Integer> {
 
  /**
   * find reportTypes
   * @return reportType list or null if not found
   */
  List<ReportType> findReportTypeByOrderByOrderId();
 
}
 