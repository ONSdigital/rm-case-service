package uk.gov.ons.ctp.response.casesvc.endpoint;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Response.Status;

import org.springframework.util.CollectionUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportType;
import uk.gov.ons.ctp.response.casesvc.representation.ReportListDTO;
import uk.gov.ons.ctp.response.casesvc.service.ReportService;

/**
 * The REST endpoint controller for CaseSvc Reports
 */
@Path("/reports")
@Produces({ "application/json" })
@Slf4j
public final class ReportEndpoint implements CTPEndpoint {

  public static final String ERRORMSG_REPORTNOTFOUND = "Report not found for";
  public static final String ERRORMSG_REPORTSNOTFOUND = "Reports not found for";
  public static final String ERRORMSG_REPORTLISTNOTFOUND = "Report Type List not found.";

  @Inject
  private ReportService reportService;

  /**
   * the GET endpoint to find all available report types
   *
   * @return List of report types
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/types")
  public Response findReportTypes() {
    log.info("Finding Report Types");
    List<ReportType> reportTypes = reportService.findTypes();

    return Response.ok(reportTypes).build();
  }

  /**
   * the GET endpoint to find list of report dates by reporttype
   *
   * @param reportType to find by
   * @return list of report dates by reportType
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{reportType}")
  public Response findReportDatesByReportType(@PathParam("reportType") final String reportType) {
    log.info("Entering findReportDatesByReportType with {}", reportType);

    List<Report> reportList = reportService.findReportDatesByReportType(reportType);

    List<ReportListDTO> reportDTOList = new ArrayList<ReportListDTO>();

    reportList.forEach(report -> reportDTOList.add(new ReportListDTO(report.getReportType().toString(), report.getReportDate())));

    ResponseBuilder responseBuilder = Response.ok(CollectionUtils.isEmpty(reportDTOList) ? null : reportDTOList);
    responseBuilder.status(CollectionUtils.isEmpty(reportDTOList) ? Status.NO_CONTENT : Status.OK);
    return responseBuilder.build();
  }

  /**
   * the GET endpoint to find report by reporttype and reportdate
   *
   * @param reporttype to find by
   * @param reportdate to find by
   * @return csv of the report found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{reportType}/{reportDate}")
  public Response findReportByReportTypeAndReportDate(@PathParam("reportType") final String reportType,
      @PathParam("reportDate") final Date reportDate) throws CTPException {
    log.info("Entering findReportByReportTypeAndReportDate with {} and {}", reportType, reportDate);

    Report reportObj = reportService.findByReportTypeAndReportDate(reportType, reportDate);

    if (reportObj == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s report type %s", ERRORMSG_REPORTNOTFOUND, reportType, reportDate.toString()));
    }

    return Response.ok(reportObj.getContents()).header("Content-Disposition",
        "attachment; filename=" + reportObj.getReportType() + "_" + reportObj.getReportDate() + ".csv").build();
  }

}
