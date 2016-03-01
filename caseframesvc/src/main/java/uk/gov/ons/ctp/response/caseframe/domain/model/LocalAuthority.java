package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "localauthority", schema = "refdata")
public class LocalAuthority implements Serializable {

  private static final long serialVersionUID = 271850419155541379L;

  @Id
  private String lad12cd;

  private String lad12nm;

  private String rgn11cd;

}
