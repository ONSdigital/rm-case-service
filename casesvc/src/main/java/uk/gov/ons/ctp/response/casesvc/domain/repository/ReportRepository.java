package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportSummary;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;

/**
 * JPA Data Repository.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, Integer> {

  /**
   * find reports by reportType
   * @param reportType to find by
   * @return reportSummary list or null if not found
   */
  @Query(value = "select new uk.gov.ons.ctp.response.casesvc.domain.model.ReportSummary(r.reportId, r.reportType, r.createdDateTime)  from Report r where r.reportType = :reportType ORDER BY createdDateTime DESC")
  List<ReportSummary> getReportSummary(@Param("reportType") ReportDTO.ReportType reportType);
 
}
 