package uk.gov.ons.ctp.response.action.export.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.service.FileService;

import javax.inject.Named;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@Slf4j
public class FileServiceImpl implements FileService {

  @Autowired
  private ResourceLoader resourceLoader;

  @Override
  public void fileMe(List<ActionRequestDocument> actionRequestDocumentList) {
    // Build the data model
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("actionRequests", actionRequestDocumentList);

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
      out.flush();

      // File output
      Writer file = new FileWriter(new File("src/main/resources/forPrinter.csv"));
      template.process(root, file);
      file.flush();
      file.close();
    } catch (IOException e) {
      log.error("IOException thrown while templating for file...", e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for file...", f.getMessage());
    }
  }
}
