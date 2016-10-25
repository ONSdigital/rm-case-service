package uk.gov.ons.ctp.response.action.export.service.impl;

import java.io.InputStream;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.repository.TemplateRepository;
import uk.gov.ons.ctp.response.action.export.service.TemplateService;

import static uk.gov.ons.ctp.common.util.InputStreamUtil.getStringFromInputStream;

/**
 * The implementation of the TemplateService
 * TODO Specific to FreeMarker at the moment with freemarker.template.Configuration, clearTemplateCache, etc.
 */
@Named
@Slf4j
public class TemplateServiceImpl implements TemplateService {

  public static final String EXCEPTION_STORE_TEMPLATE = "Issue storing TemplateDocument. It appears to be empty.";

  @Inject
  private TemplateRepository repository;

  @Inject
  private freemarker.template.Configuration configuration;

  @Override
  public TemplateDocument retrieveTemplateDocument(String templateName) {
    return repository.findOne(templateName);
  }

  @Override
  public List<TemplateDocument> retrieveAllTemplateDocuments() {
    return repository.findAll();
  }

  @Override
  public TemplateDocument storeTemplateDocument(String templateName, InputStream fileContents) throws CTPException {
    String stringValue = getStringFromInputStream(fileContents);
    if (StringUtils.isEmpty(stringValue)) {
      log.error(EXCEPTION_STORE_TEMPLATE);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, EXCEPTION_STORE_TEMPLATE);
    }

    TemplateDocument template = new TemplateDocument();
    template.setContent(stringValue);
    template.setName(templateName);
    template.setDateModified(new Date());
    template = repository.save(template);

    configuration.clearTemplateCache();

    return template;
  }
}
