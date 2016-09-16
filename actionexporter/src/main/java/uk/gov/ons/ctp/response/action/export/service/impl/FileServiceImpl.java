package uk.gov.ons.ctp.response.action.export.service.impl;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequest;
import uk.gov.ons.ctp.response.action.export.service.FileService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named
@Slf4j
public class FileServiceImpl implements FileService {

  @Inject
  private Configuration configuration;

  @Override
  public void fileMe(List<ActionRequest> actionRequestList) {
    // Build the data model
    Map<String, Object> root = new HashMap<String, Object>();
    root.put("actionRequests", actionRequestList);

    try {
      Template template = configuration.getTemplate("csvExport.ftl"); // Configuration caches Template instances

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
