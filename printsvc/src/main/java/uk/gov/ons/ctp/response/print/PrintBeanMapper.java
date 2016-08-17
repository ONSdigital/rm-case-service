package uk.gov.ons.ctp.response.print;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.impl.generator.EclipseJdtCompilerStrategy;

/**
 * The bean mapper to go from Entity objects to Presentation objects.
 */
@Named
public class PrintBeanMapper extends ConfigurableMapper {

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
//    factory
//        .classMap(Action.class, ActionDTO.class)
//        .field("actionType.name", "actionTypeName")
//        .byDefault()
//        .register();

  }
}
