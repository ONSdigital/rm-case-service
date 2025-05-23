package uk.gov.ons.ctp.response.casesvc.domain.model;

import jakarta.persistence.*;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.Parameter;
import org.hibernate.type.SqlTypes;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/** Domain model object. */
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
  @GenericGenerator(
      name = "caseeventseq_gen",
      strategy = "org.hibernate.id.enhanced.SequenceStyleGenerator",
      parameters = {
        @Parameter(name = "sequence_name", value = "casesvc.caseeventseq"),
        @Parameter(name = "increment_size", value = "1")
      })
  @Column(name = "case_event_pk")
  private Integer caseEventPK;

  @Column(name = "case_fk")
  private Integer caseFK;

  private String description;

  @Column(name = "created_by")
  private String createdBy;

  @Column(name = "created_date_time")
  private Timestamp createdDateTime;

  @Enumerated(EnumType.STRING)
  @Column(name = "category_fk")
  private CategoryDTO.CategoryName category;

  @Column(name = "subcategory")
  private String subCategory;

  @Column(name = "metadata")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, String> metadata;
}
