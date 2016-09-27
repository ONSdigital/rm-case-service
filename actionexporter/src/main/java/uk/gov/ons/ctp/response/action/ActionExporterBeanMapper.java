package uk.gov.ons.ctp.response.action;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.domain.FreeMarkerTemplate;
import uk.gov.ons.ctp.response.action.export.templating.freemarker.representation.FreeMarkerTemplateDTO;

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
            .classMap(FreeMarkerTemplate.class, FreeMarkerTemplateDTO.class)
            .byDefault()
            .register();
  }
}
