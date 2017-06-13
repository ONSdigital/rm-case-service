package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

import uk.gov.ons.ctp.response.iac.representation.InternetAccessCodeDTO;

/**
 * The Service to generate/disable IACs
 */
public interface InternetAccessCodeSvcClientService {

  /**
   * To generate IACs
   * @param count the number of IACs to generate
   * @return a list of IACs
   */
  List<String> generateIACs(int count);

  /**
   * To diable an IAC
   * @param iac the one to disable
   * @return
   */
  InternetAccessCodeDTO disableIAC(String iac);
    
}