package uk.gov.ons.ctp.response.caseframe.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;

/**
 * JPA Data Repository
 */
@Repository
public interface MsoaRepository extends JpaRepository<Msoa, String> {
  public List<Msoa> findByLad12cdOrderByMsoa11nm(String ladid);
}
