package uk.gov.ons.ctp.response.casesvc.domain.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Domain model object for cases generated in Case schema.
 */
@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GeneratedCase implements Serializable {

  private static final long serialVersionUID = -798186055190065296L;

  @Id
  @Column(name = "p_caseid_out")
  private Integer caseId;

  @Column(name = "p_actionplanid_out")
  private Integer actionPlanId;

}
