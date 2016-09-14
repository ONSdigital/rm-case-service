package uk.gov.ons.ctp.response.action.export.service;


import fr.opensagres.xdocreport.converter.ConverterTypeTo;
import fr.opensagres.xdocreport.converter.ConverterTypeVia;
import fr.opensagres.xdocreport.converter.Options;
import fr.opensagres.xdocreport.core.XDocReportException;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import fr.opensagres.xdocreport.template.formatter.FieldsMetadata;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequest;

import java.io.*;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class to investigate best option for templating - CTPA-700
 */
public class TemplateInvestigation {
  public static void main(String[] args) throws IOException, TemplateException, XDocReportException {
    /**
     * Step - Get the action requests from the MongoDB
     */
    List<ActionRequest> actionRequestList = buildMeListOfActionRequests();
    System.out.println(String.format("We have %d action requests...", actionRequestList.size()));

    /**
     * Step - Produce a csv file
     */
    //Freemarker configuration object
    Configuration cfg = new Configuration();
    Template template = cfg.getTemplate("actionexporter/src/main/resources/templates/csvExport.ftl");

    // Build the data-model
    Map<String, Object> data = new HashMap<String, Object>();
    data.put("actionRequests", actionRequestList);

    // Console output
    Writer out = new OutputStreamWriter(System.out);
    template.process(data, out);
    out.flush();

    // File output
    Writer file = new FileWriter(new File("actionexporter/src/main/resources/forPrinter.csv"));
    template.process(data, file);
    file.flush();
    file.close();

    // Pdf example
    OutputStream pdfOutput = new FileOutputStream("actionexporter/src/main/resources/forPrinter.pdf");
    writeAsPdf(buildAMeActionRequest(1), pdfOutput);
  }

  /**
   * TODO This will be replaced by a actionRequestRepo.findAll or similar
   */
  private static List<ActionRequest> buildMeListOfActionRequests() {
    List<ActionRequest> result = new ArrayList<>();
    for (int i = 1; i < 51; i++) {
      result.add(buildAMeActionRequest(i));
    }
    return result;
  }

  private static ActionRequest buildAMeActionRequest(int i) {
    ActionRequest result =  new ActionRequest();
    result.setActionId(new BigInteger(new Integer(i).toString()));
    result.setActionType("testActionType");
    result.setIac("testIac");
    return result;
  }

  private static void writeAsPdf(ActionRequest actionRequest, OutputStream out)
          throws IOException, XDocReportException {
    InputStream in = getDefaultTemplate();

    // Prepare the IXDocReport instance based on the template, using Freemarker template engine
    IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker);

    // Define what we want to do (PDF file from ODF template)
    Options options = Options.getTo(ConverterTypeTo.PDF).via(ConverterTypeVia.ODFDOM);

    // Add properties to the context
    IContext ctx = report.createContext();
    ctx.put("actionRequest", actionRequest);
    // TODO ctx.put("to", actionRequest.getTo());
    // TODO ctx.put("sender", invoice.getInvoicer());

    // instruct XDocReport to inspect InvoiceRow entity as well which is given as list and iterated in a table
    FieldsMetadata metadata = report.createFieldsMetadata();
    // TODO metadata.load("r", InvoiceRow.class, true);
    // TODO ctx.put("r", invoice.getInvoiceRows());

    // Write the PDF file to output stream
    report.convert(ctx, options, out);
    out.close();
  }

  private static InputStream getDefaultTemplate() {
    // TODO modify this .odt so it matches our ActionRequest fields
    return ActionRequest.class.getResourceAsStream("/tmpl.odt");
  }
}