package uk.gov.ons.ctp.response.action.export.service;

import freemarker.cache.TemplateLoader;

import java.io.IOException;
import java.io.Reader;

public class CustomTemplateLoader implements TemplateLoader {
  @Override
  public Object findTemplateSource(String s) throws IOException {
    return null;
  }

  @Override
  public long getLastModified(Object o) {
    return 0;
  }

  @Override
  public Reader getReader(Object o, String s) throws IOException {
    return null;
  }

  @Override
  public void closeTemplateSource(Object o) throws IOException {

  }
}
