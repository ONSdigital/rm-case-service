package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "category", schema = "casesvc")
public class Category implements Serializable {
  private static final long serialVersionUID = 2310849817220604095L;

  @Id
  @Column(name = "categoryid")
  private Integer categoryId;

  private String name;

  private String description;

  private String role;

  private String group;

  @Column(name = "generatedactiontype")
  private String generatedActionType;

  @Column(name = "closecase")
  private Boolean closeCase;

  private Boolean manual;

}
