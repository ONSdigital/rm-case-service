package uk.gov.ons.ctp.response.action.export.templating.freemarker.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.repository.FreeMarkerTemplateRepository;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.service.impl.FreeMarkerServiceImpl;

import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.action.export.templating.freemarker.service.impl.FreeMarkerServiceImpl.EXCEPTION_STORE_TEMPLATE;

/**
 * To unit test FreeMarkerServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class FreeMarkerServiceImplTest {
  @InjectMocks
  FreeMarkerServiceImpl freeMarkerService;

  @Mock
  FreeMarkerTemplateRepository repository;

  @Mock
  freemarker.template.Configuration configuration;

  private static final String TEMPLATE_NAME = "testTemplate";

  @Test
  public void testClearTemplateCache() {
    freeMarkerService.clearTemplateCache();
    verify(configuration, times(1)).clearTemplateCache();
  }

  @Test
  public void testStoreNullTemplate() {
    boolean exceptionThrown = false;
    try {
      freeMarkerService.storeTemplate(TEMPLATE_NAME, null);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_STORE_TEMPLATE, e.getMessage());
    }
    assertTrue(exceptionThrown);
    verify(repository, times(0)).save(any(FreeMarkerTemplate.class));
  }

  @Test
  public void testStoreEmptyTemplate() {
    boolean exceptionThrown = false;
    try {
      freeMarkerService.storeTemplate(TEMPLATE_NAME, getClass().getResourceAsStream("/templates/freemarker/curltest_emptytemplate.ftl"));
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_STORE_TEMPLATE, e.getMessage());
    }
    assertTrue(exceptionThrown);
    verify(repository, times(0)).save(any(FreeMarkerTemplate.class));
  }

  @Test
  public void testStoreValidTemplate() throws CTPException {
    freeMarkerService.storeTemplate(TEMPLATE_NAME, getClass().getResourceAsStream("/templates/freemarker/curltest_validtemplate.ftl"));
    verify(repository, times(1)).save(any(FreeMarkerTemplate.class));
  }

}
