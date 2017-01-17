package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportSummary;
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportType;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;

public interface ReportService extends CTPService {

  /**
   * find all available report types
   *
   * @return List of report types
   */
  List<ReportType> findTypes();

  /**
   * Find reports by reportType.
   *
   * @param reportType String enum
   * @return Report list object or null
   */
  List<ReportSummary> getReportSummary(ReportDTO.ReportType reportType);

  /**
   * Find Report entity by reportId.
   *
   * @param reportId Integer
   * @return Report object or null
   */
  Report findByReportId(Integer reportId);

}
