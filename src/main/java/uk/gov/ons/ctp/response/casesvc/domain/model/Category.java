package uk.gov.ons.ctp.response.casesvc.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/** Domain model object. */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category", schema = "casesvc")
public class Category implements Serializable {

  private static final long serialVersionUID = -8265556287097172790L;

  @Id
  @Column(name = "category_pk")
  @Enumerated(EnumType.STRING)
  private CategoryDTO.CategoryName categoryName;

  @Column(name = "long_description")
  private String longDescription;

  @Column(name = "short_description")
  private String shortDescription;

  private String role;

  private String group;

  @Column(name = "event_type")
  @Enumerated(EnumType.STRING)
  private CaseDTO.CaseEvent eventType;

  @Column(name = "old_case_sample_unit_types")
  private String oldCaseSampleUnitTypes;

  @Column(name = "new_case_sample_unit_type")
  private String newCaseSampleUnitType;

  @Column(name = "generated_action_type")
  private String generatedActionType;

  @Column(name = "recalc_collection_instrument")
  private Boolean recalcCollectionInstrument;
}
