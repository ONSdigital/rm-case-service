package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;

import javax.inject.Named;

/**
 * JPA Data Repository.
 */
@Named
public interface LocalAuthorityRepository extends JpaRepository<LocalAuthority, String> {
  /**
   * find the LocalAuthorities for a given region, ordered by the LA name.
   * @param regionId identity of the region whose LAs we want
   * @return the list of local authorities found ordered by name
   */
  List<LocalAuthority> findByRgn11cdOrderByLad12nm(String regionId);
}
