package uk.gov.ons.ctp.response.casesvc.endpoint;

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
import ma.glasnost.orika.MapperFacade;
import uk.gov.ons.ctp.common.endpoint.CTPEndpoint;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.casesvc.domain.model.Report;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDetailDTO;
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
  
  @Inject
  private MapperFacade mapperFacade;

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
    List<ReportDTO.ReportType> reportTypes = reportService.findTypes();

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
  @Path("/{reportType}/details")
  public Response findReportDatesByReportType(@PathParam("reportType") final ReportDTO.ReportType reportType) {
    log.info("Entering findReportDatesByReportType with {}", reportType);

    List<Report> reports = reportService.findReportDatesByReportType(reportType);
    List<ReportDetailDTO> reportList = mapperFacade.mapAsList(reports, ReportDetailDTO.class);


    ResponseBuilder responseBuilder = Response.ok(CollectionUtils.isEmpty(reportList) ? null : reportList);
    responseBuilder.status(CollectionUtils.isEmpty(reportList) ? Status.NO_CONTENT : Status.OK);
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
  @Path("/{reportId}")
  public Response findReportByReportId(@PathParam("reportId") final int reportId) throws CTPException {
    log.info("Entering findReportByReportId with {}", reportId);

    Report report = reportService.findByReportId(reportId);

    if (report == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s report type %s", ERRORMSG_REPORTNOTFOUND, reportId));
    }
    return Response.ok(report.getContents()).build();
  }

}
