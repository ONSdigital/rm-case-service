package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.sourceforge.cobertura.CoverageIgnore;
import uk.gov.ons.ctp.response.casesvc.representation.CaseDTO;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/** Domain model object. */
@CoverageIgnore
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category", schema = "casesvc")
public class Category implements Serializable {

  private static final long serialVersionUID = -8265556287097172790L;

  @Id
  @Column(name = "categorypk")
  @Enumerated(EnumType.STRING)
  private CategoryDTO.CategoryName categoryName;

  @Column(name = "longdescription")
  private String longDescription;

  @Column(name = "shortdescription")
  private String shortDescription;

  private String role;

  private String group;

  @Column(name = "eventtype")
  @Enumerated(EnumType.STRING)
  private CaseDTO.CaseEvent eventType;

  @Column(name = "oldcasesampleunittypes")
  private String oldCaseSampleUnitTypes;

  @Column(name = "newcasesampleunittype")
  private String newCaseSampleUnitType;

  @Column(name = "generatedactiontype")
  private String generatedActionType;

  @Column(name = "recalccollectioninstrument")
  private Boolean recalcCollectionInstrument;
}
