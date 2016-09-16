package uk.gov.ons.ctp.response.action.export.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.repository.FreeMarkerTemplateRepository;
import uk.gov.ons.ctp.response.action.export.service.FreeMarkerService;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import static org.glassfish.jersey.message.internal.ReaderWriter.UTF8;

@Named
@Slf4j
public class FreeMarkerServiceImpl implements FreeMarkerService {

  public static final String EXCEPTION_STORE_TEMPLATE = "Issue storing FreeMarker template - ";

  @Inject
  private FreeMarkerTemplateRepository repository;

  @Override
  public FreeMarkerTemplate retrieveTemplate(String templateName) {
    return repository.findOne(templateName);
  }

  @Override
  public FreeMarkerTemplate storeTemplate(String templateName, MultipartFile file) throws CTPException {
    FreeMarkerTemplate template = new FreeMarkerTemplate();
    template.setName(templateName);

    template.setDateModified(new Date());

    // from MultipartFile to String
    try {
      ByteArrayInputStream stream = new ByteArrayInputStream(file.getBytes());
      String stringValue = IOUtils.toString(stream, UTF8.name());
      template.setContent(stringValue);
      return repository.save(template);
    } catch (IOException e) {
      String theError = String.format("%s%s", EXCEPTION_STORE_TEMPLATE, e.getMessage());
      log.error(theError);
      throw new CTPException(CTPException.Fault.SYSTEM_ERROR, theError);
    }
  }

}
