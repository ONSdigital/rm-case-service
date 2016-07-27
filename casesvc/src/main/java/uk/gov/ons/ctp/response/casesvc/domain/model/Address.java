package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object.
 */
@Entity
@Data
@NoArgsConstructor(access = AccessLevel.PUBLIC)
@Table(name = "address", schema = "casesvc")
public class Address implements Serializable {

  private static final long serialVersionUID = 4831221877955672383L;

  @Id
  private Long uprn;

  @Column(name = "addresstype")
  private String type;

  @Column(name = "estabtype")
  private String estabType;

  private String locality;

  private String organisationName;

  private String category;

  @Column(name = "address_line1")
  private String line1;

  @Column(name = "address_line2")
  private String line2;

  private String townName;

  private String postcode;

  private String oa11cd;

  private String lsoa11cd;

  private String msoa11cd;

  private String lad12cd;

  private String region11cd;

  private Integer eastings;

  private Integer northings;

  private Integer htc;

  private Double latitude;

  private Double longitude;

}
