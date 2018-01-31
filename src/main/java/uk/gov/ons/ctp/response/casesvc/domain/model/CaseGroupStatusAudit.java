package uk.gov.ons.ctp.response.casesvc.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.ons.ctp.response.casesvc.representation.CaseGroupStatus;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.sql.Timestamp;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "casegroupstatusaudit", schema = "casesvc")
public class CaseGroupStatusAudit {

    @Column(name = "casegroupfk")
    private int caseGroupFK;

    @Column(name = "createdby")
    private String createdBy;

    @Column(name = "status")
    private CaseGroupStatus status;

    @Column(name = "createddatetime")
    private Timestamp createdDateTime;
}
