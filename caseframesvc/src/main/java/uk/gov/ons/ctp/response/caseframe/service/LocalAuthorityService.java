package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.LocalAuthority;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;

/**
 * The interface defining the function of the LocalAuthority service.
 * The application business logic should reside in it's implementation
 */
public interface LocalAuthorityService extends CTPService {
    LocalAuthority findById(String ladid);

    /**
     * Returns all MSOAs for a given ladid  sorted by MSOA name ascending
     */
    List<Msoa> findAllMsoasByLadid(String ladid);
}
