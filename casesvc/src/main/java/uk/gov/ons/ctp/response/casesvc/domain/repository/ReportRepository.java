package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.sql.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO.ReportType;

/**
 * JPA Data Repository.
 */
@Repository
public interface ReportRepository extends JpaRepository<Report, String> {

  /**
   * find report dates by reportType
   * @param reportType to find by
   * @return the report or null if not found
   */
  List<Report> findByReportType(ReportType reportType);

  /**
   * find Report by reportType and reportDate
   * @param reportType to find by
   * @param reportDate to find by
   * @return the report or null if not found
   */
  Report findByReportTypeAndReportDate(ReportType reportType, Date reportDate);

}
