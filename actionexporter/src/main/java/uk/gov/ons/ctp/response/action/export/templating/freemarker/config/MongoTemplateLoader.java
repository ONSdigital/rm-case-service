package uk.gov.ons.ctp.response.action.export.templating.freemarker.config;

import freemarker.cache.TemplateLoader;
import lombok.extern.slf4j.Slf4j;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;
import uk.gov.ons.ctp.response.action.export.repository.ContentRepository;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

@Slf4j
@Named
public class MongoTemplateLoader implements TemplateLoader {

  @Inject
  private ContentRepository contentRepository;

  @Override
  public Object findTemplateSource(String name) throws IOException {
    log.debug("Retrieving template with name {}", name);
    return contentRepository.findOne(name);
  }

  @Override
  public long getLastModified(Object templateSource) {
    ContentDocument template = (ContentDocument) templateSource;
    String name = template.getName();
    log.debug("Retrieving last modified time for template with name {}", name);
    template = contentRepository.findOne(name);
    return template.getDateModified().getTime();
  }

  @Override
  public Reader getReader(Object templateSource, String encoding) throws IOException {
    // TODO encoding will be UTF-8 -do we need to do anything with it?
    return new StringReader(((ContentDocument) templateSource).getContent());
  }

  @Override
  public void closeTemplateSource(Object templateSource) throws IOException {
  }
}
