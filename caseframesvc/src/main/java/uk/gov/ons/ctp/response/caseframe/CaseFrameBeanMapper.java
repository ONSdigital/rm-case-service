package uk.gov.ons.ctp.response.caseframe;

import javax.inject.Named;

import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.impl.ConfigurableMapper;
import uk.gov.ons.ctp.response.caseframe.domain.model.Address;
import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;
import uk.gov.ons.ctp.response.caseframe.domain.model.Case;
import uk.gov.ons.ctp.response.caseframe.domain.model.CaseType;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;
import uk.gov.ons.ctp.response.caseframe.domain.model.QuestionSet;
import uk.gov.ons.ctp.response.caseframe.domain.model.Questionnaire;
import uk.gov.ons.ctp.response.caseframe.domain.model.Region;
import uk.gov.ons.ctp.response.caseframe.domain.model.Sample;
import uk.gov.ons.ctp.response.caseframe.domain.model.Survey;
import uk.gov.ons.ctp.response.caseframe.representation.AddressDTO;
import uk.gov.ons.ctp.response.caseframe.representation.AddressSummaryDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseDTO;
import uk.gov.ons.ctp.response.caseframe.representation.CaseTypeDTO;
import uk.gov.ons.ctp.response.caseframe.representation.LocalAuthorityDTO;
import uk.gov.ons.ctp.response.caseframe.representation.MsoaDTO;
import uk.gov.ons.ctp.response.caseframe.representation.QuestionSetDTO;
import uk.gov.ons.ctp.response.caseframe.representation.QuestionnaireDTO;
import uk.gov.ons.ctp.response.caseframe.representation.RegionDTO;
import uk.gov.ons.ctp.response.caseframe.representation.SampleDTO;
import uk.gov.ons.ctp.response.caseframe.representation.SurveyDTO;

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
        .byDefault()
        .register();

    factory
        .classMap(CaseType.class, CaseTypeDTO.class)
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
        .byDefault()
        .register();

    factory
        .classMap(Survey.class, SurveyDTO.class)
        .byDefault()
        .register();

  }
}
