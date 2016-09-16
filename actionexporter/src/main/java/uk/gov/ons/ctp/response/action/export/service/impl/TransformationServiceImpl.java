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

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;

@Named
@Slf4j
public class TransformationServiceImpl implements TransformationService {

  @Autowired
  private ResourceLoader resourceLoader;

  @Override
  public File fileMe(List<ActionRequestDocument> actionRequestDocumentList, String path) {
    File resultFile = new File(path);
    Writer fileWriter = null;
    try {
      fileWriter = new FileWriter(resultFile);
      Template template = giveMeTemplate();
      template.process(buildDataModel(actionRequestDocumentList), fileWriter);
    } catch (IOException e) {
      log.error("IOException thrown while templating for file...", e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for file...", f.getMessage());
    } finally {
      if (fileWriter != null) {
        try {
          fileWriter.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing the file writer...", e.getMessage());
        }
      }
    }

    return resultFile;
  }

  @Override
  public ByteArrayOutputStream streamMe(List<ActionRequestDocument> actionRequestDocumentList) {
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Writer outputStreamWriter = null;
    try {
      outputStreamWriter = new OutputStreamWriter(outputStream);
      Template template = giveMeTemplate();
      template.process(buildDataModel(actionRequestDocumentList), outputStreamWriter);
      outputStreamWriter.close();
    } catch (IOException e) {
      log.error("IOException thrown while templating for stream...", e.getMessage());
    } catch (TemplateException f) {
      log.error("TemplateException thrown while templating for stream...", f.getMessage());
    } finally {
      if (outputStreamWriter != null) {
        try {
          outputStreamWriter.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing the output stream writer...", e.getMessage());
        }
      }
    }

    return outputStream;
  }

  private Template giveMeTemplate() throws IOException {
    // TODO get templates from db: follow http://www.nurkiewicz.com/2010/01/writing-custom-freemarker-template.html
    Configuration cfg = new freemarker.template.Configuration(freemarker.template.Configuration.VERSION_2_3_25);
    File templateDirectory = resourceLoader.getResource("classpath:templates").getFile();
    cfg.setDirectoryForTemplateLoading(templateDirectory);  // non-file-system sources are possible too: see setTemplateLoader();
    cfg.setDefaultEncoding(UTF8.name());
    cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
    // Don't log exceptions inside FreeMarker that it will thrown at you anyway:
    cfg.setLogTemplateExceptions(false);

    Template template = cfg.getTemplate("csvExport.ftl"); // Configuration caches Template instances
    return template;
  }

  private Map<String, Object> buildDataModel(List<ActionRequestDocument> actionRequestDocumentList) {
    Map<String, Object> result = new HashMap<String, Object>();
    result.put("actionRequests", actionRequestDocumentList);
    return result;
  }
}
