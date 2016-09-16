package uk.gov.ons.ctp.response.action.export.freemarker;

import freemarker.cache.TemplateLoader;
import uk.gov.ons.ctp.response.action.export.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.repository.FreeMarkerTemplateRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@Named
public class MongoTemplateLoader implements TemplateLoader {

//  @Inject
//  private FreeMarkerTemplateRepository freeMarkerTemplateRepository;


  @Override
  public Object findTemplateSource(String name) throws IOException {
    //TODO return freeMarkerTemplateRepository.findOne(name);
    return null;
  }

  @Override
  public long getLastModified(Object templateSource) {
    FreeMarkerTemplate template = (FreeMarkerTemplate) templateSource;
    //TODO template = freeMarkerTemplateRepository.findOne(template.getName());
    return template.getDateModified().getTime();
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    // TODO encoding will be UTF-8 -do we need to do anything with it?
    return new StringReader(((FreeMarkerTemplate) templateSource).getContent());
  }

  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {
  }
}
