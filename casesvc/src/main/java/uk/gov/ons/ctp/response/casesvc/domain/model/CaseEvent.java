package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "caseevent", schema = "casesvc")
public class CaseEvent implements Serializable {

  private static final long serialVersionUID = 6034836141646834386L;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY, generator = "caseeventseq_gen")
  @SequenceGenerator(name = "caseeventseq_gen", sequenceName = "casesvc.caseeventidseq")
  @Column(name = "caseeventid")
  private Integer caseEventId;

  @Column(name = "caseid")
  private Integer caseId;

  private String description;

  @Column(name = "createdby")
  private String createdBy;

  @Column(name = "createddatetime")
  private Timestamp createdDateTime;

  @Enumerated(EnumType.STRING)
  private CategoryDTO.CategoryType category;

  @Column(name = "subcategory")
  private String subCategory;
}

