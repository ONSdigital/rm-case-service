package uk.gov.ons.ctp.response.action.export.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.TransformationService;

import javax.inject.Named;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@Slf4j
public class TransformationServiceImpl implements TransformationService {

  @Autowired
  private ResourceLoader resourceLoader;

  @Override
  public File fileMe(List<ActionRequestDocument> actionRequestDocumentList, String path) {
    // Build the data model
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("actionRequests", actionRequestDocumentList);

    File resultFile = new File(path);
    try {
      // TODO get templates from db: follow http://www.nurkiewicz.com/2010/01/writing-custom-freemarker-template.html
      Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
      File templateDirectory = resourceLoader.getResource("classpath:templates").getFile();
      cfg.setDirectoryForTemplateLoading(templateDirectory);  // non-file-system sources are possible too: see setTemplateLoader();
      cfg.setDefaultEncoding("UTF-8");
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
      cfg.setLogTemplateExceptions(false);

      Template template = cfg.getTemplate("csvExport.ftl"); // Configuration caches Template instances

      // Console output
      Writer out = new OutputStreamWriter(System.out);
      template.process(root, out);
      out.close();

      // File output
      Writer fileWriter = new FileWriter(resultFile);
      template.process(root, fileWriter);
      fileWriter.close();
    } catch (IOException e) {
      log.error("IOException thrown while templating for file...", e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for file...", f.getMessage());
    }
    return resultFile;
  }

  @Override
  public OutputStream streamMe(List<ActionRequestDocument> actionRequestDocumentList) {
    // Build the data model
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("actionRequests", actionRequestDocumentList);

    OutputStream outputStream = new ByteArrayOutputStream();
    try {
      // TODO get templates from db: follow http://www.nurkiewicz.com/2010/01/writing-custom-freemarker-template.html
      Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
      File templateDirectory = resourceLoader.getResource("classpath:templates").getFile();
      cfg.setDirectoryForTemplateLoading(templateDirectory);  // non-file-system sources are possible too: see setTemplateLoader();
      cfg.setDefaultEncoding("UTF-8");
      cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
      // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
      cfg.setLogTemplateExceptions(false);

      Template template = cfg.getTemplate("csvExport.ftl"); // Configuration caches Template instances

      // Console output
      Writer out = new OutputStreamWriter(System.out);
      template.process(root, out);
      out.close();

      // Streaming
      Writer outputStreamWriter = new OutputStreamWriter(outputStream);
      template.process(root, outputStreamWriter);
      outputStreamWriter.close();
    } catch (IOException e) {
      log.error("IOException thrown while templating for file...", e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for file...", f.getMessage());
    }

    return outputStream;
  }
}
