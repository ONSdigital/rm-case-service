package uk.gov.ons.ctp.response.casesvc.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.casesvc.domain.model.GeneratedCase;

/**
 * JPA Data Repository
 */
@Repository
public interface CaseLifeCycleRepository extends JpaRepository<GeneratedCase, Integer> {

  /**
   * Stored procedure to generate new cases for given sample ID, geography type
   * and geography code
   *
   * @param sampleId the sample id
   * @param geographyType the geography type
   * @param geographyCode the geography code
   * @return List of GeneratedCases
   */
  @Query(value = "select * from casesvc.generate_cases(:p_sampleid, :p_geog_area_type, :p_geog_area_code)",
      nativeQuery = true)
  List<GeneratedCase> generateCases(@Param("p_sampleid") Integer sampleId,
      @Param("p_geog_area_type") String geographyType,
      @Param("p_geog_area_code") String geographyCode);

}
