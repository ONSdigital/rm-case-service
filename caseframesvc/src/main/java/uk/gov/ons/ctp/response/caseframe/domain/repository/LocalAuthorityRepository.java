package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;

import javax.inject.Named;

/**
 * JPA Data Respository
 */
@Named
public interface LocalAuthorityRepository extends JpaRepository<LocalAuthority, String> {
    List<LocalAuthority> findByRgn11cdOrderByLad12nm(String regionId);
}
