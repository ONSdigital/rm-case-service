package uk.gov.ons.ctp.response.casesvc.domain.model;

import javax.persistence.*;

/**
 * The below is for (taken from https://github.com/spring-projects/spring-data-examples/tree/master/jpa/jpa21)
 * DROP procedure IF EXISTS plus1inout
 /;
 CREATE procedure plus1inout (IN arg int, OUT res int)
 BEGIN ATOMIC
 set res = arg + 1;
 END
 /;
 */
// TODO CTPA-1409
@Entity
@NamedStoredProcedureQuery(name = "CaseReport.plus1inout", procedureName = "plus1inout", parameters = {
        @StoredProcedureParameter(mode = ParameterMode.IN, name = "arg", type = Integer.class),
        @StoredProcedureParameter(mode = ParameterMode.OUT, name = "res", type = Integer.class) })
public class CaseReport {
    @Id @GeneratedValue//
    private Long id;
}
