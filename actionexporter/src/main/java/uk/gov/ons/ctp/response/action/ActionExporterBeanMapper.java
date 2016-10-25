package uk.gov.ons.ctp.response.action;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import uk.gov.ons.ctp.response.action.export.domain.ContentDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;
import uk.gov.ons.ctp.response.action.representation.TemplateDocumentDTO;
import uk.gov.ons.ctp.response.action.representation.TemplateMappingDocumentDTO;

/**
 * The bean mapper to go from Entity objects to Presentation objects.
 */
@Named
public class ActionExporterBeanMapper extends ConfigurableMapper {

  @Override
  public void configureFactoryBuilder(DefaultMapperFactory.Builder builder) {
    builder.compilerStrategy(new EclipseJdtCompilerStrategy());
  }

  /**
   * This method configures the bean mapper.
   *
   * @param factory the mapper factory
   */
  @Override
  protected final void configure(final MapperFactory factory) {
    factory
            .classMap(TemplateDocument.class, TemplateDocumentDTO.class)
            .field("templateEngine", "type")
            .byDefault()
            .register();

    factory
            .classMap(TemplateMappingDocument.class, TemplateMappingDocumentDTO.class)
            .field("templateEngine", "type")
            .byDefault()
            .register();
  }
}
