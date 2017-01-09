package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.sql.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportType;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ReportRepository;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ReportTypeRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;
import uk.gov.ons.ctp.response.casesvc.service.ReportService;

@Slf4j
@Named
public class ReportServiceImpl implements ReportService {

  /**
   * Spring Data Repository for CSV Report entities.
   */
    @Inject
    ReportRepository reportRepository;

    @Inject
    ReportTypeRepository reportTypeRepository;

    /**
     * find all available report types
     *
     * @return List of report types
     */
    public List<ReportType> findTypes() {
      List<ReportType> reportTypes = reportTypeRepository.findAll();
      return reportTypes;
    }

    /**
     * Find report dates by reportType.
     *
     * @param reportType String
     * @return Report object or null
     */
    @Override
    public List<Report> findReportDatesByReportType(final String reportType) {
      log.debug("Entering findReportDatesByReportType with {}", reportType);
      return reportRepository.findByReportType(ReportDTO.ReportType.valueOf(reportType));
    }

    /**
     * Find Report entity by reportType.
     *
     * @param reportType String
     * @return Report object or null
     */
    @Override
    public Report findByReportTypeAndReportDate(final String reportType, final Date reportDate) {
      log.debug("Entering findByReportTypeAndReportDate with {}", reportType + " " + reportDate);
      return reportRepository.findByReportTypeAndReportDate(ReportDTO.ReportType.valueOf(reportType), reportDate);
    }

}
