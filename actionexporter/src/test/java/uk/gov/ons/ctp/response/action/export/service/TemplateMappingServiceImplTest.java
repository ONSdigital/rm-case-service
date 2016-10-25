package uk.gov.ons.ctp.response.action.export.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import uk.gov.ons.ctp.common.error.CTPException;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;
import uk.gov.ons.ctp.response.action.export.repository.TemplateMappingRepository;
import uk.gov.ons.ctp.response.action.export.service.impl.TemplateMappingServiceImpl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static uk.gov.ons.ctp.response.action.export.service.impl.TemplateMappingServiceImpl.EXCEPTION_STORE_TEMPLATE_MAPPING;

/**
 * To unit test TemplateMappingServiceImpl
 */
@RunWith(MockitoJUnitRunner.class)
public class TemplateMappingServiceImplTest {

  private static final String TEMPLATE_MAPPING_NAME = "testTemplateMapping";

  @InjectMocks
  TemplateMappingServiceImpl templateMappingService;

  @Mock
  TemplateMappingRepository repository;

  @Test
  public void testStoreNullTemplateMapping() {
    boolean exceptionThrown = false;
    try {
      templateMappingService.storeTemplateMappingDocument(TEMPLATE_MAPPING_NAME, null);
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_STORE_TEMPLATE_MAPPING, e.getMessage());
    }
    assertTrue(exceptionThrown);
    verify(repository, times(0)).save(any(TemplateMappingDocument.class));
  }

  @Test
  public void testStoreEmptyTemplateMapping() {
    boolean exceptionThrown = false;
    try {
      templateMappingService.storeTemplateMappingDocument(TEMPLATE_MAPPING_NAME,getClass().getResourceAsStream("/templates/freemarker/empty_template_mapping.json"));
    } catch (CTPException e) {
      exceptionThrown = true;
      assertEquals(CTPException.Fault.SYSTEM_ERROR, e.getFault());
      assertEquals(EXCEPTION_STORE_TEMPLATE_MAPPING, e.getMessage());
    }
    assertTrue(exceptionThrown);
    verify(repository, times(0)).save(any(TemplateMappingDocument.class));
  }

  @Test
  public void testStoreValidTemplateMapping() throws CTPException {
    templateMappingService.storeTemplateMappingDocument(TEMPLATE_MAPPING_NAME, getClass().getResourceAsStream("/templates/freemarker/valid_template_mapping.json"));
    verify(repository, times(1)).save(any(TemplateMappingDocument.class));
  }

}
