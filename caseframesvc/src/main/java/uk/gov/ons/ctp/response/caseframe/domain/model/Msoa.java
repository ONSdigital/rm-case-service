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
@Table(name="msoa", schema="refdata")
public class Msoa implements Serializable {

  private static final long serialVersionUID = -7880051861582046804L;

    @Id
    private String msoa11cd;

    private String msoa11nm;

    private String lad12cd;

}
