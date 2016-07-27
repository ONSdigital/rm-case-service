package uk.gov.ons.ctp.response.casesvc.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.query.Procedure;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.Sample;

/**
 * JPA Data Repository
 */
@Repository
public interface SampleRepository extends JpaRepository<Sample, Integer> {

  /**
   * Stored procedure to generate new cases for given sample ID, geography type
   * and geography code
   * @param sampleid the sample id
   * @param geographyType the geography type
   * @param geographyCode the geography code
   * @return the number of cases generated
   */
  @Procedure(name = "generate_cases")
  boolean generateCases(@Param("p_sampleid") Integer sampleid, @Param("p_geog_area_type") String geographyType,
      @Param("p_geog_area_code") String geographyCode);
}
