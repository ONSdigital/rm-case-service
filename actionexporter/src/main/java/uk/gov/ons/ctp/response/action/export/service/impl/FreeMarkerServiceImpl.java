package uk.gov.ons.ctp.response.action.export.service.impl;

import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.repository.FreeMarkerTemplateRepository;
import uk.gov.ons.ctp.response.action.export.service.FreeMarkerService;

import javax.inject.Inject;
import javax.inject.Named;

@Named
@Slf4j
public class FreeMarkerServiceImpl implements FreeMarkerService {

  @Inject
  private FreeMarkerTemplateRepository repository;

  @Override
  public FreeMarkerTemplate storeTemplate(FreeMarkerTemplate template) {
    return repository.save(template);
  }

  @Override
  public FreeMarkerTemplate retrieveTemplate(String templateName) {
    return repository.findOne(templateName);
  }
}
