package uk.gov.ons.ctp.response.caseframe;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import uk.gov.ons.ctp.response.caseframe.domain.model.*;
import uk.gov.ons.ctp.response.caseframe.representation.*;

/**
 * The bean mapper that maps to/from DTOs and JPA entity types.
 *
 */
@Named
public class CaseFrameBeanMapper extends ConfigurableMapper {

  /**
   * Setup the mapper for all of our beans. Only fields having non  identical names need
   * mapping if we also use byDefault() following.
   * @param factory the factory to which we add our mappings
   */
  protected final void configure(final MapperFactory factory) {

    factory
        .classMap(Address.class, AddressDTO.class)
        .field("oa11cd", "outputArea")
        .field("lsoa11cd", "lsoaArea")
        .field("msoa11cd", "msoaArea")
        .field("lad12cd", "ladCode")
        .field("region11cd", "regionCode")
        .byDefault()
        .register();

    factory
        .classMap(AddressSummary.class, AddressSummaryDTO.class)
        .byDefault()
        .register();

    factory
        .classMap(Case.class, CaseDTO.class)
        .field("status", "state")
        .byDefault()
        .register();

    factory
        .classMap(CaseEvent.class, CaseEventDTO.class)
        .byDefault()
        .register();

    factory
        .classMap(CaseType.class, CaseTypeDTO.class)
        .field("name", "caseTypeName")
        .byDefault()
        .register();

    factory
        .classMap(LocalAuthority.class, LocalAuthorityDTO.class)
        .field("lad12cd", "ladCode")
        .field("lad12nm", "ladName")
        .field("rgn11cd", "regionCode")
        .byDefault()
        .register();

    factory
        .classMap(Msoa.class, MsoaDTO.class)
        .field("msoa11cd", "msoaCode")
        .field("msoa11nm", "msoaName")
        .field("lad12cd", "ladCode")
        .byDefault()
        .register();

    factory
        .classMap(Questionnaire.class, QuestionnaireDTO.class)
        .field("status", "questionnaireStatus")
        .byDefault()
        .register();

    factory
        .classMap(QuestionSet.class, QuestionSetDTO.class)
        .byDefault()
        .register();

    factory
        .classMap(Region.class, RegionDTO.class)
        .field("rgn11cd", "regionCode")
        .field("rgn11nm", "regionName")
        .byDefault()
        .register();

    factory
        .classMap(Sample.class, SampleDTO.class)
        .field("name", "sampleName")
        .byDefault()
        .register();

    factory
        .classMap(Survey.class, SurveyDTO.class)
        .field("name", "surveyName")
        .byDefault()
        .register();

  }
}
