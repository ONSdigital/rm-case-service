package uk.gov.ons.ctp.response.action.export.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;
import uk.gov.ons.ctp.response.action.export.repository.ContentRepository;
import uk.gov.ons.ctp.response.action.export.service.DocumentService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.*;
import java.util.Date;
import java.util.List;

@Named
@Slf4j
public class DocumentServiceImpl implements DocumentService {

  public static final String EXCEPTION_STORE_TEMPLATE = "Issue storing ContentDocument. It appears to be empty.";

  @Inject
  private ContentRepository repository;

  @Inject
  private freemarker.template.Configuration configuration;

  @Override
  public ContentDocument retrieveContentDocument(String contentDocumentName) {
    return repository.findOne(contentDocumentName);
  }

  @Override
  public List<ContentDocument> retrieveAllContentDocuments() {
    return repository.findAll();
  }

  @Override
  public ContentDocument storeContentDocument(String contentDocumentName, InputStream fileContents) throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE);
    }
    ContentDocument template = new ContentDocument();
    template.setContent(stringValue);

    template.setName(contentDocumentName);

    template.setDateModified(new Date());

    return repository.save(template);
  }

  @Override
  public void clearTemplateCache() {
    configuration.clearTemplateCache();
    log.debug("Free Marker template cache has been cleared.");
  }

  private static String getStringFromInputStream(InputStream is) {
    BufferedReader br = null;
    String line;
    StringBuilder sb = new StringBuilder();
    try {
      br = new BufferedReader(new InputStreamReader(is));
      while ((line = br.readLine()) != null) {
        sb.append(line);
      }
    } catch (Exception e) {
      log.error("Exception thrown while converting template stream to string - msg = {}", e.getMessage());
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
          log.error("IOException thrown while closing buffered reader used to convert template stream - msg = {}",
                  e.getMessage());
        }
      }
    }

    return sb.toString();
  }

}
