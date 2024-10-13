package uk.gov.ons.ctp.response.casesvc.domain.model;

//import com.vladmihalcea.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
//import org.hibernate.annotations.TypeDef;
//import org.hibernate.annotations.TypeDefs;
import jakarta.persistence.Convert;
import org.hibernate.annotations.JdbcTypeCode;
import uk.gov.ons.ctp.response.casesvc.representation.CategoryDTO;

/** Domain model object. */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
//@TypeDefs({@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)})
@Convert(attributeName = "entityAttrName", converter = JsonBinaryType.class)
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
  @Type(type = "jsonb")
  private Map<String, String> metadata;
}
