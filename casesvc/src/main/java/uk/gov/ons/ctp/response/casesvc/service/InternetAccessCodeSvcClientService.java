package uk.gov.ons.ctp.response.casesvc.service;

import java.util.List;

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
   * To disable an IAC
   * @param iac the one to disable
   */
  void disableIAC(String iac);
}
