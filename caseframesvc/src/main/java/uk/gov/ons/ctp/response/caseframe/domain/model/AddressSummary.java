package uk.gov.ons.ctp.response.caseframe.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object
 */
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "address", schema = "caseframe")
public class AddressSummary implements Serializable {

  private static final long serialVersionUID = 4655252949670397034L;

  @Id
  private Long uprn;

  @Column(name = "addresstype")
  private String addressType;

  @Column(name = "estabtype")
  private String estabType;

  private String addressLine1;

  private String addressLine2;

  private String townName;

  private String postcode;

  private String msoa11cd;
}
