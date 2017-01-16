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
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportSummary;
import uk.gov.ons.ctp.response.casesvc.domain.model.ReportType;
import uk.gov.ons.ctp.response.casesvc.representation.ReportDTO;
import uk.gov.ons.ctp.response.casesvc.representation.ReportSummaryDTO;
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
  public Response findReportTypes() throws CTPException {
    log.info("Finding Report Types");
    List<ReportType> reportTypes = reportService.findTypes();

    if (reportTypes == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND, "Report types not found");
    }
    
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
  @Path("/types/{reportType}")
  public Response findReportDatesByReportType(@PathParam("reportType") final ReportDTO.ReportType reportType) throws CTPException {
    log.info("Entering findReportDatesByReportType with {}", reportType);

    List<ReportSummary> reports = reportService.getReportSummary(reportType);
    List<ReportSummaryDTO> reportList = mapperFacade.mapAsList(reports, ReportSummaryDTO.class);

    if (reportList == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s report type %s", ERRORMSG_REPORTSNOTFOUND, reportType));
    }
    
    ResponseBuilder responseBuilder = Response.ok(CollectionUtils.isEmpty(reportList) ? null : reportList);
    responseBuilder.status(CollectionUtils.isEmpty(reportList) ? Status.NO_CONTENT : Status.OK);
    return responseBuilder.build();
  }

  /**
   * the GET endpoint to find report by reporttype and reportdate
   *
   * @param reportId to find by
   * @return the report found
   * @throws CTPException something went wrong
   */
  @GET
  @Path("/{reportId}")
  public Response findReportByReportId(@PathParam("reportId") final int reportId) throws CTPException {
    log.info("Entering findReportByReportId with {}", reportId);

    Report report = reportService.findByReportId(reportId);
    ReportDTO reportDTO = mapperFacade.map(report, ReportDTO.class);
    
    if (report == null) {
      throw new CTPException(CTPException.Fault.RESOURCE_NOT_FOUND,
          String.format("%s report type %s", ERRORMSG_REPORTNOTFOUND, reportId));
    }
    return Response.ok(reportDTO).build();
  }

}
