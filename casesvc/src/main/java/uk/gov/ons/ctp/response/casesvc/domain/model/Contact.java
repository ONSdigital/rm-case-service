package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
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
@Table(name = "contact", schema = "casesvc")
public class Contact implements Serializable {

  @Id
  @GeneratedValue
  @Column(name = "contactid")
  private Integer contactId;
  
  @Column(name = "forename")
  private String forename;
  
  @Column(name = "surname")
  private String surname;
   
  @Column(name = "phonenumber")
  private String phoneNumber;

  @Column(name = "emailaddress")
  private String emailAddress;
}
