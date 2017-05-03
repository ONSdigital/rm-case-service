package uk.gov.ons.ctp.response.casesvc;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import uk.gov.ons.ctp.response.casesvc.domain.model.Case;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseEvent;
import uk.gov.ons.ctp.response.casesvc.domain.model.CaseGroup;
import uk.gov.ons.ctp.response.casesvc.domain.model.Category;
import uk.gov.ons.ctp.response.casesvc.domain.model.Response;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseEventDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;
import uk.gov.ons.ctp.response.casesvc.representation.ResponseDTO;

/**
 * The bean mapper that maps to/from DTOs and JPA entity types.
 *
 */
@Named
public class CaseSvcBeanMapper extends ConfigurableMapper {

  /**
   * Setup the mapper for all of our beans. Only fields having non  identical names need
   * mapping if we also use byDefault() following.
   * @param factory the factory to which we add our mappings
   */
  protected final void configure(final MapperFactory factory) {

    factory
        .classMap(Case.class, CaseDTO.class)
        .byDefault()
        .register();

    factory
        .classMap(CaseGroup.class, CaseGroupDTO.class)
        .byDefault()
        .register();

    factory
        .classMap(CaseEvent.class, CaseEventDTO.class)
        .byDefault()
        .register();

    factory
        .classMap(Category.class, CategoryDTO.class)
        .field("categoryType", "name")
        .byDefault()
        .register();

    factory
        .classMap(Response.class, ResponseDTO.class)
        .byDefault()
        .register();
  }
}
