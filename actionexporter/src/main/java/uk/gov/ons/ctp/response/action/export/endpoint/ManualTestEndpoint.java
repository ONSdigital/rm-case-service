package uk.gov.ons.ctp.response.action.export.endpoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;
import uk.gov.ons.ctp.response.action.message.instruction.ActionAddress;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;

/**
 * Convenient endpoint to test manually our POC with FreeMarker templates pulled from the MongoDB
 *
 * Once integration tests work correctly, the endpoint will be removed.
 */
@Controller
@Path("/manualtest")
@Produces(MediaType.APPLICATION_JSON)
@Slf4j
public class ManualTestEndpoint {

  private static final int ACTION_REQUEST_NUMBER = 51;

  @Inject
  private TemplateService templateService;

  /**
   * To trigger the FreeMarker templating of a list of ActionRequests
   * @param templateName the name of the Template to use for templating
   * @return OK if the templating completed successfully
   * @throws CTPException if issue encountered during the FreeMarker templating
   * @throws UnsupportedEncodingException if issue encountered during the FreeMarker templating
   */
  @GET
  @Path("/{templateName}")
  public final Response testingFreeMarkerTemplating(@PathParam("templateName") final String templateName)
          throws CTPException, UnsupportedEncodingException {
    log.debug("Entering testingFreeMarkerTemplating ...");
    ByteArrayOutputStream result = templateService.stream(buildMeListOfActionRequestDocuments(), templateName);
    String resultString = result.toString(UTF8.name());
    log.debug("resultString = {}", resultString);

    if (!StringUtils.isEmpty(resultString)) {
      return Response.status(Response.Status.OK).build();
    } else {
      return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
    }
  }

  /**
   * To build a list of ActionRequests
   * @return a list of ActionRequests
   */
  private static List<ActionRequestDocument> buildMeListOfActionRequestDocuments() {
    List<ActionRequestDocument> result = new ArrayList<>();
    for (int i = 1; i < ACTION_REQUEST_NUMBER; i++) {
      result.add(buildAMeActionRequestDocument(i));
    }
    return result;
  }

  /**
   *  To build an ActionRequest
   * @param i the action id for the ActionRequest
   * @return an ActionRequest
   */
  private static ActionRequestDocument buildAMeActionRequestDocument(int i) {
    ActionRequestDocument result =  new ActionRequestDocument();
    result.setActionId(new BigInteger(new Integer(i).toString()));
    result.setActionType("testActionType");
    result.setIac("testIac");
    result.setAddress(buildActionAddress());
    return result;
  }

  /**
   * To build an ActionAddress
   * @return an ActionAddress
   */
  private static ActionAddress buildActionAddress() {
    ActionAddress actionAddress = new ActionAddress();
    actionAddress.setLine1("1 High Street");
    actionAddress.setTownName("Southampton");
    actionAddress.setPostcode("SO16 0AS");
    return actionAddress;
  }
}
