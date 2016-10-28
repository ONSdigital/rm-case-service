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

import java.util.Date;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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

  @Test
  public void testRetrieveMapFromNonExistingTemplateMappingDocument() {
    Map<String, String> result = templateMappingService.retrieveMapFromTemplateMappingDocument(TEMPLATE_MAPPING_NAME);
    assertNotNull(result);
    assertTrue(result.isEmpty());
    verify(repository, times(1)).findOne(TEMPLATE_MAPPING_NAME);
  }

  @Test
  public void testRetrieveMapFromExistingTemplateMappingDocument() {
    TemplateMappingDocument templateMappingDocument = new TemplateMappingDocument();
    templateMappingDocument.setName(TEMPLATE_MAPPING_NAME);
    templateMappingDocument.setDateModified(new Date());
    templateMappingDocument.setContent("{\n" +
            "  \"ICL1\":\"curltest1\",\n" +
            "  \"ICL2\":\"curltest2\",\n" +
            "  \"ICL2W\":\"curltest3\"}");
    when(repository.findOne(TEMPLATE_MAPPING_NAME)).thenReturn(templateMappingDocument);

    Map<String, String> result = templateMappingService.retrieveMapFromTemplateMappingDocument(TEMPLATE_MAPPING_NAME);
    assertNotNull(result);
    assertEquals(3, result.size());
    assertEquals("curltest1", result.get("ICL1"));
    assertEquals("curltest2", result.get("ICL2"));
    assertEquals("curltest3", result.get("ICL2W"));
    verify(repository, times(1)).findOne(TEMPLATE_MAPPING_NAME);
  }
}
