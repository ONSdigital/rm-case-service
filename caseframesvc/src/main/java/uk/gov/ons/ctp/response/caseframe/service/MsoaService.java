package uk.gov.ons.ctp.response.caseframe.service;

import java.util.List;

import uk.gov.ons.ctp.common.service.CTPService;
import uk.gov.ons.ctp.response.caseframe.domain.model.AddressSummary;
import uk.gov.ons.ctp.response.caseframe.domain.model.Msoa;

/**
 * The interface defining the function of the Msoa service.
 * The application business logic should reside in it's implementation
 */
public interface MsoaService extends CTPService {
    Msoa findById(String msoaid);
    List<AddressSummary> findAllAddressSummariesByMsoaid(String msoaid);
}
