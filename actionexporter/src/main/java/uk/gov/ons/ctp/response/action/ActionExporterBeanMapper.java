package uk.gov.ons.ctp.response.action;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import uk.gov.ons.ctp.response.action.export.domain.ActionRequestDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateDocument;
import uk.gov.ons.ctp.response.action.export.domain.TemplateMappingDocument;
import uk.gov.ons.ctp.response.action.export.representation.ActionRequestDocumentDTO;
import uk.gov.ons.ctp.response.action.export.representation.TemplateDocumentDTO;
import uk.gov.ons.ctp.response.action.export.representation.TemplateMappingDocumentDTO;

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
            .byDefault()
            .register();

    factory
            .classMap(TemplateMappingDocument.class, TemplateMappingDocumentDTO.class)
            .byDefault()
            .register();

    factory
            .classMap(ActionRequestDocument.class, ActionRequestDocumentDTO.class)
            .byDefault()
            .register();
  }
}
