package uk.gov.ons.ctp.response.caseframe.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.ons.ctp.response.caseframe.domain.model.Region;

import javax.inject.Named;
import java.util.List;

/**
 * JPA Data Respository
 */
@Named
public interface RegionRepository extends JpaRepository<Region, String> {
  List<Region> findAllByOrderByRgn11cd();
}
