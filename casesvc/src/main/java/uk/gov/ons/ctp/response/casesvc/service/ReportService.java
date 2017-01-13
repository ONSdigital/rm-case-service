package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;

public interface ReportService extends CTPService {

  /**
   * find all available report types
   *
   * @return List of report types
   * @throws CTPException something went wrong
   */
  List<ReportDTO.ReportType> findTypes();

  /**
   * find report dates by reportType.
   *
   * @param reportType to find by
   * @return list of report dates by reportType or null if not found
   */
  List<Report> findReportDatesByReportType(ReportDTO.ReportType reportType);

  /**
   * find Report by reportType and reportDate.
   *
   * @param reportType and reportDate to find by
   * @return the report or null if not found
   */
  Report findByReportId(Integer reportId);

}
