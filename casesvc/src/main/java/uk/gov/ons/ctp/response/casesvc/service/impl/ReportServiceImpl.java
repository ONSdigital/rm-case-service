package uk.gov.ons.ctp.response.casesvc.service.impl;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.domain.repository.ReportRepository;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;
import uk.gov.ons.ctp.response.casesvc.service.ReportService;

@Slf4j
@Named
public class ReportServiceImpl implements ReportService {

  /**
   * Spring Data Repository for CSV Report entities.
   */
    @Inject
    private ReportRepository reportRepository;

    /**
     * find all available report types
     *
     * @return List of report types
     */
    public List<ReportDTO.ReportType> findTypes() {
      List<ReportDTO.ReportType> reportTypes = Arrays.asList(ReportDTO.ReportType.values());
      return reportTypes;
    }

    /**
     * Find reports by reportType.
     *
     * @param reportType String enum
     * @return Report list object or null
     */
    @Override
    public List<Report> findReportWithoutContentByReportType(final ReportDTO.ReportType reportType) {
      log.debug("Entering findReportDatesByReportType with {}", reportType);
      return reportRepository.findByReportTypeWithoutContents(ReportDTO.ReportType.valueOf(reportType.toString()));
    }

    /**
     * Find Report entity by reportId.
     *
     * @param reportId Integer
     * @return Report object or null
     */
    @Override
    public Report findByReportId(final Integer reportId) {
      log.debug("Entering findByReportTypeAndReportDate with {}", reportId);
      return reportRepository.findOne(reportId);
    }

}
